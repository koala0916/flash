package com.powernode.dispatch.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.powernode.common.constant.RedisConstant;
import com.powernode.dispatch.client.XxlJobClient;
import com.powernode.dispatch.mapper.OrderJobMapper;
import com.powernode.dispatch.service.NewOrderService;
import com.powernode.map.client.LocationFeignClient;
import com.powernode.model.entity.dispatch.OrderJob;
import com.powernode.model.enums.OrderStatus;
import com.powernode.model.form.map.SearchNearByDriverForm;
import com.powernode.model.vo.dispatch.NewOrderTaskVo;
import com.powernode.model.vo.map.NearByDriverVo;
import com.powernode.model.vo.order.NewOrderDataVo;
import com.powernode.order.client.OrderInfoFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class NewOrderServiceImpl implements NewOrderService {

    @Resource
    private OrderJobMapper orderJobMapper;

    @Resource
    private XxlJobClient xxlJobClient;

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    @Resource
    private LocationFeignClient locationFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 1.根据订单id查询当前订单任务
     * 2.若没有则插入数据库
     * @param newOrderTaskVo
     * @return
     */
    @Override
    public Long addAndStartTask(NewOrderTaskVo newOrderTaskVo) {
        //根据id查询订单任务
        LambdaQueryWrapper<OrderJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderJob::getOrderId, newOrderTaskVo.getOrderId());
        OrderJob orderJob = orderJobMapper.selectOne(queryWrapper);

        //若没有则创建
        if (orderJob == null) {
            //向xxl-job添加定时任务
            Long jobId = xxlJobClient.addAndStart("newOrderTaskHandler", "", "0/5 * * * * ?", "新订单任务");

            //向orderJob表中添加数据
            orderJob = new OrderJob();
            orderJob.setOrderId(newOrderTaskVo.getOrderId());
            orderJob.setJobId(jobId);
            orderJob.setCreateTime(new Date());
            //设置订单任务参数  json
            orderJob.setParameter(JSONObject.toJSONString(newOrderTaskVo));

            orderJobMapper.insert(orderJob);

        }
        return orderJob.getJobId();
    }


    /**
     * 执行定时任务
     * 1.查询任务参数
     * 2.查询订单状态
     * 3.搜索附近配送员
     * 4.给配送员派单
     */
    @Override
    public Boolean executeTask(Long jobId) {
        //1.查询任务参数
        LambdaQueryWrapper<OrderJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderJob::getJobId, jobId);
        OrderJob orderJob = orderJobMapper.selectOne(queryWrapper);

        if (orderJob == null) {
            return true;
        }

        //获取了任务参数对象
        String jsonStr = orderJob.getParameter();
        NewOrderTaskVo newOrderTaskVo = JSONObject.parseObject(jsonStr, NewOrderTaskVo.class);

        //2.查询订单状态 若订单状态为取消，则不执行
        Integer orderStatus = orderInfoFeignClient.getOrderStatus(newOrderTaskVo.getOrderId()).getData();

        if (orderStatus.intValue() != OrderStatus.WAITING_ACCEPT.getStatus()){
            //停止定时任务
            xxlJobClient.stopJob(jobId);
            return true;
        }

        //3.搜索附近配送员
        SearchNearByDriverForm searchNearByDriverForm = new SearchNearByDriverForm();
        searchNearByDriverForm.setLongitude(newOrderTaskVo.getStartPointLongitude());
        searchNearByDriverForm.setLatitude(newOrderTaskVo.getStartPointLatitude());
        searchNearByDriverForm.setMileageDistance(newOrderTaskVo.getExpectDistance());

        //查看符合条件的配送员
        List<NearByDriverVo> nearByDriverVoList = locationFeignClient.searchNearByDriver(searchNearByDriverForm).getData();

        //遍历集合
        nearByDriverVoList.forEach(driver -> {
            //同一个配送员只派单1次  订单编号
            String orderKey = RedisConstant.DRIVER_ORDER_REPEAT_LIST + newOrderTaskVo.getOrderId();

            Boolean member = redisTemplate.opsForSet().isMember(orderKey, driver.getDriverId());
            if (!member) {
                //若进入这里则说明该配送员还没有被派单

                //派单  设置过期时间 16分钟
                redisTemplate.expire(orderKey, RedisConstant.DRIVER_ORDER_REPEAT_LIST_EXPIRES_TIME, TimeUnit.MINUTES);

                //将订单数据保存到redis的list中
                NewOrderDataVo newOrderDataVo = new NewOrderDataVo();

                BeanUtils.copyProperties(newOrderTaskVo, newOrderDataVo);

                //将派单信息放入配送员的缓存中，配送员小程序会轮训查询该数据
                String driverKey = RedisConstant.DRIVER_ORDER_TEMP_LIST + driver.getDriverId();

                //将订单信息放入到配送员id对应的list中
                redisTemplate.opsForList().leftPush(driverKey,JSONObject.toJSONString(newOrderDataVo));

                //设置过期时间  防止订单信息刚刚生成，此时配送员下班
                redisTemplate.expire(driverKey, RedisConstant.DRIVER_ORDER_TEMP_LIST_EXPIRES_TIME, TimeUnit.MINUTES);

                //标记配送员已被派单
                redisTemplate.opsForSet().add(orderKey, driver.getDriverId());
            }
        });
        return true;
    }

}


