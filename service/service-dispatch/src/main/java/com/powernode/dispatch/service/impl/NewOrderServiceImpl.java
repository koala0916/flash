package com.powernode.dispatch.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.powernode.dispatch.client.XxlJobClient;
import com.powernode.dispatch.mapper.OrderJobMapper;
import com.powernode.dispatch.service.NewOrderService;
import com.powernode.model.entity.dispatch.OrderJob;
import com.powernode.model.vo.dispatch.NewOrderTaskVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;


@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class NewOrderServiceImpl implements NewOrderService {

    @Resource
    private OrderJobMapper orderJobMapper;

    @Resource
    private XxlJobClient xxlJobClient;

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
            //设置订单任务参数
            orderJob.setParameter(JSONObject.toJSONString(newOrderTaskVo));

            orderJobMapper.insert(orderJob);

        }
        return orderJob.getJobId();
    }
}
