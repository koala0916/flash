package com.powernode.order.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.common.constant.RedisConstant;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.entity.order.OrderStatusLog;
import com.powernode.model.enums.OrderStatus;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.order.mapper.OrderInfoMapper;
import com.powernode.order.mapper.OrderStatusLogMapper;
import com.powernode.order.service.OrderInfoService;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private OrderStatusLogMapper orderStatusLogMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private RedissonClient redissonClient;


    /**
     * 用户下订单
     */
    @Transactional
    @Override
    public Long addOrderInfo(OrderInfoForm orderInfoForm) {
        //创建订单
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderInfoForm, orderInfo);

        //创建订单编号
        String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
        orderInfo.setOrderNo(orderNo);

        //设置订单状态  接单状态
        orderInfo.setStatus(OrderStatus.WAITING_ACCEPT.getStatus());

        //添加订单
        orderInfoMapper.insert(orderInfo);

        //记录日志
        OrderStatusLog orderStatusLog = new OrderStatusLog();
        orderStatusLog.setOrderId(orderInfo.getId());
        orderStatusLog.setOrderStatus(orderInfo.getStatus());
        orderStatusLog.setOperateTime(new Date());

        //日志添加数据库
        orderStatusLogMapper.insert(orderStatusLog);

        //将订单状态放入redis中 表示是否被接单
        redisTemplate.opsForValue().set(RedisConstant.ORDER_ACCEPT_MARK + orderInfo.getId(), orderInfo.getStatus(), RedisConstant.ORDER_ACCEPT_MARK_EXPIRES_TIME, TimeUnit.MINUTES);

        return orderInfo.getId();
    }


    /**
     * 查询订单状态
     */
    @Override
    public Integer queryOrderStatus(Long orderId) {
        LambdaQueryWrapper<OrderInfo> orderInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderInfoLambdaQueryWrapper.select(OrderInfo::getStatus);//只查询状态字段
        orderInfoLambdaQueryWrapper.eq(OrderInfo::getId, orderId);//根据订单主键查询
        OrderInfo orderInfo = orderInfoMapper.selectOne(orderInfoLambdaQueryWrapper);

        return orderInfo.getStatus();
    }


    /**
     * 配送员抢单
     * 1.判断订单状态(判断该订单状态数据在redis中是否还存在)  是否已被接单
     * 2.若可以接单，则修改订单状态为已接单
     * 3.删除redis中的订单
     */
    @Transactional
    @Override
    public Boolean robNewOrder(Long driverId, Long orderId) {

        //判断redis中的订单状态是否还存在
        Boolean orderStatus = redisTemplate.hasKey(RedisConstant.ORDER_ACCEPT_MARK + orderId);
        if (!orderStatus) {
            //抢单失败  订单已经被别人抢走了
            throw new PowerException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
        }


        //使用redisson实现分布式锁
        RLock lock = redissonClient.getLock(RedisConstant.ROB_NEW_ORDER_LOCK + orderId);

        boolean flag = false;
        try {
            //尝试获取锁,尝试1秒,若没有获取到锁 则放弃 可以走服务降级 告诉配送员抢单失败
            flag = lock.tryLock(RedisConstant.ROB_NEW_ORDER_LOCK_WAIT_TIME, RedisConstant.ROB_NEW_ORDER_LOCK_LEASE_TIME, TimeUnit.SECONDS);

            if (flag) {
                //若程序走到这里，则说明获取锁成功
                //再次判断redis的订单状态是否存在
                orderStatus = redisTemplate.hasKey(RedisConstant.ORDER_ACCEPT_MARK + orderId);
                if (!orderStatus) {
                    //抢单失败  订单已经被别人抢走了
                    throw new PowerException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
                }
            }

            //抢单  若程序走到这里，则说明有资格可以抢单
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setId(orderId);
            orderInfo.setAcceptTime(new Date());//配送员接单时间
            orderInfo.setStatus(OrderStatus.ACCEPTED.getStatus());//修改订单状态为已接单
            orderInfo.setDriverId(driverId);//接单的配送员id

            int num = orderInfoMapper.updateById(orderInfo);
            if (num != 1) {
                //修改订单失败
                throw new PowerException(ResultCodeEnum.COB_NEW_ORDER_FAIL);
            }

            //记录日志
            OrderStatusLog orderStatusLog = new OrderStatusLog();
            orderStatusLog.setOrderId(orderInfo.getId());
            orderStatusLog.setOrderStatus(orderInfo.getStatus());
            orderStatusLog.setOperateTime(new Date());
            orderStatusLogMapper.insert(orderStatusLog);


            //配送员抢单后删除redis中的该订单数据
            redisTemplate.delete(RedisConstant.ORDER_ACCEPT_MARK + orderInfo.getId());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            //释放锁  判断锁是否被当前线程持有
            if (lock.isHeldByCurrentThread() && lock.isLocked()) {
                lock.unlock();
            }

        }


        return true;
    }
}
