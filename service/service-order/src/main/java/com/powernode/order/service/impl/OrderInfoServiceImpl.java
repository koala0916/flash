package com.powernode.order.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.common.constant.RedisConstant;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.entity.order.OrderStatusLog;
import com.powernode.model.enums.OrderStatus;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.order.mapper.OrderInfoMapper;
import com.powernode.order.mapper.OrderStatusLogMapper;
import com.powernode.order.service.OrderInfoService;
import jakarta.annotation.Resource;
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
        redisTemplate.opsForValue().set(RedisConstant.ORDER_ACCEPT_MARK+orderInfo.getId(), orderInfo.getStatus(), RedisConstant.ORDER_ACCEPT_MARK_EXPIRES_TIME, TimeUnit.MINUTES);

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
}
