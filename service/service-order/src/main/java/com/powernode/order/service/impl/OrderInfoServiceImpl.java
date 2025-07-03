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
import com.powernode.model.form.order.StartDriveForm;
import com.powernode.model.form.order.UpdateOrderCartForm;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
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


    /**
     * 查询当前进行中的订单
     * @param customerId
     * @return
     */
    @Override
    public CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getCustomerId, customerId);

        //下面这些订单状态需要自动打开订单页面
        Integer[] statusArray = {
                OrderStatus.ACCEPTED.getStatus(),
                OrderStatus.DRIVER_ARRIVED.getStatus(),
                OrderStatus.UPDATE_CART_INFO.getStatus(),
                OrderStatus.START_SERVICE.getStatus(),
                OrderStatus.END_SERVICE.getStatus(),
                OrderStatus.UNPAID.getStatus()
        };

        queryWrapper.in(OrderInfo::getStatus, statusArray);
        //结果排序
        queryWrapper.orderByDesc(OrderInfo::getId);

        //获取第一个订单
        queryWrapper.last("limit 1");

        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();

        if (orderInfo != null) {
            //说明当前有正在进行中的订单
            currentOrderInfoVo.setIsHasCurrentOrder(true);
            currentOrderInfoVo.setOrderId(orderInfo.getId());
            currentOrderInfoVo.setStatus(orderInfo.getStatus());
        }else {
            //当前没有符合条件的订单信息
            currentOrderInfoVo.setIsHasCurrentOrder(false);
        }

        return currentOrderInfoVo;


    }

    /**
     * 配送员查看当前进行中的订单
     */
    @Override
    public CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getDriverId, driverId);
        //配送员发送完账单，以下这些订单状态时都会自动打开订单页面
        Integer[] statusArray = {
                OrderStatus.ACCEPTED.getStatus(),
                OrderStatus.DRIVER_ARRIVED.getStatus(),
                OrderStatus.UPDATE_CART_INFO.getStatus(),
                OrderStatus.START_SERVICE.getStatus(),
                OrderStatus.END_SERVICE.getStatus()
        };

        queryWrapper.in(OrderInfo::getStatus, statusArray);
        queryWrapper.orderByDesc(OrderInfo::getId);
        queryWrapper.last("limit 1");
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        CurrentOrderInfoVo currentOrderInfoVo = new CurrentOrderInfoVo();

        if (orderInfo != null) {
            //说明当前有正在进行中的订单
            currentOrderInfoVo.setIsHasCurrentOrder(true);
            currentOrderInfoVo.setOrderId(orderInfo.getId());
            currentOrderInfoVo.setStatus(orderInfo.getStatus());
        }else {
            //当前没有符合条件的订单信息
            currentOrderInfoVo.setIsHasCurrentOrder(false);
        }

        return currentOrderInfoVo;
    }


    /**
     * 配送员到达指定位置
     */
    @Override
    public Boolean driverArrivedStartLocation(Long orderId, Long driverId) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, orderId);
        queryWrapper.eq(OrderInfo::getDriverId, driverId);

        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setStatus(OrderStatus.DRIVER_ARRIVED.getStatus());//修改订单状态为配送员已到达
        updateOrderInfo.setArriveTime(new Date());//配送员到达的时间
        int num = orderInfoMapper.update(updateOrderInfo, queryWrapper);

        if (num == 1){
            //记录日志 这里省略
        }else {
            throw new PowerException(ResultCodeEnum.UPDATE_ERROR);
        }

        return true;
    }


    /**
     * 更新配送员车辆信息
     */
    @Override
    public Boolean updateDriverCarInfo(UpdateOrderCartForm updateOrderCartForm) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, updateOrderCartForm.getOrderId());
        queryWrapper.eq(OrderInfo::getDriverId, updateOrderCartForm.getDriverId());

        //修改数据库的订单信息
        OrderInfo updateOrderInfo = new OrderInfo();

        BeanUtils.copyProperties(updateOrderCartForm, updateOrderInfo);
        //这里与数据库对不上
        updateOrderInfo.setStatus(OrderStatus.UPDATE_CART_INFO.getStatus());

        int num = orderInfoMapper.update(updateOrderInfo, queryWrapper);
        if (num == 1){
            //记录日志 这里省略
        }else {
            throw new PowerException(ResultCodeEnum.UPDATE_ERROR);
        }

        return true;
    }


    /**
     * 修改状态为开始配送
     */
    @Override
    public Boolean startDrive(StartDriveForm startDriveForm) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, startDriveForm.getOrderId());
        queryWrapper.eq(OrderInfo::getDriverId, startDriveForm.getDriverId());

        OrderInfo updateOrderInfo = new OrderInfo();

        //修改状态为开始配送
        updateOrderInfo.setStatus(OrderStatus.START_SERVICE.getStatus());

        updateOrderInfo.setStartServiceTime(new Date());
        int num = orderInfoMapper.update(updateOrderInfo, queryWrapper);

        if (num == 1){
            //记录日志 这里省略
        }else {
            throw new PowerException(ResultCodeEnum.UPDATE_ERROR);
        }

        return true;
    }

}
