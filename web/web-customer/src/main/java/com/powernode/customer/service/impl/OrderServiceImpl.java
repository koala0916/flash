package com.powernode.customer.service.impl;


import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.customer.service.OrderService;
import com.powernode.dispatch.client.NewOrderFeignClient;
import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.map.client.LocationFeignClient;
import com.powernode.map.client.MapFeignClient;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.form.customer.ExpectOrderForm;
import com.powernode.model.form.customer.SubmitOrderForm;
import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.model.form.rules.FeeRuleRequestForm;
import com.powernode.model.vo.customer.ExpectOrderVo;
import com.powernode.model.vo.dispatch.NewOrderTaskVo;
import com.powernode.model.vo.driver.DriverInfoVo;
import com.powernode.model.vo.map.DrivingLineVo;
import com.powernode.model.vo.map.OrderLocationVo;
import com.powernode.model.vo.map.OrderServiceLastLocationVo;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.model.vo.order.OrderInfoVo;
import com.powernode.model.vo.rules.FeeRuleResponseVo;
import com.powernode.order.client.OrderInfoFeignClient;
import com.powernode.rules.client.FeeRuleFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Resource
    private MapFeignClient mapFeignClient;

    @Resource
    private FeeRuleFeignClient feeRuleFeignClient;

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;
    @Autowired
    private NewOrderFeignClient newOrderFeignClient;
    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;
    @Autowired
    private LocationFeignClient locationFeignClient;

    /**
     * 预估订单费用和路线
     * @param exerciseOrderForm
     * @return
     */
    @Override
    public ExpectOrderVo expectOrder(ExpectOrderForm exerciseOrderForm) {
        //计算配送路线
        CalculateDrivingLineForm calculateDrivingLineForm = new CalculateDrivingLineForm();
        BeanUtils.copyProperties(exerciseOrderForm, calculateDrivingLineForm);
        DrivingLineVo drivingLineVo = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();

        //预估订单费用
        FeeRuleRequestForm feeRuleRequestForm = new FeeRuleRequestForm();
        feeRuleRequestForm.setDistance(drivingLineVo.getDistance());
        feeRuleRequestForm.setStartTime(new Date());
        feeRuleRequestForm.setWaitMinute(0);
        FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(feeRuleRequestForm).getData();

        //创建返回结果对象
        ExpectOrderVo expectOrderVo = new ExpectOrderVo();
        expectOrderVo.setDrivingLineVo(drivingLineVo);
        expectOrderVo.setFeeRuleResponseVo(feeRuleResponseVo);

        return expectOrderVo;
    }


    /**
     * 下订单
     */
    @Override
    public Long addOrder(SubmitOrderForm submitOrderForm) {
        //重新计算路线
        CalculateDrivingLineForm calculateDrivingLineForm = new CalculateDrivingLineForm();

        BeanUtils.copyProperties(submitOrderForm, calculateDrivingLineForm);
        DrivingLineVo drivingLineVo = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();

        //重新计算费用
        FeeRuleRequestForm feeRuleRequestForm = new FeeRuleRequestForm();
        feeRuleRequestForm.setDistance(drivingLineVo.getDistance());
        feeRuleRequestForm.setStartTime(new Date());
        feeRuleRequestForm.setWaitMinute(0);
        FeeRuleResponseVo feeRuleResponseVo = feeRuleFeignClient.calculateOrderFee(feeRuleRequestForm).getData();

        OrderInfoForm orderInfoForm = new OrderInfoForm();
        BeanUtils.copyProperties(submitOrderForm, orderInfoForm);
        //预估价格
        orderInfoForm.setExpectAmount(feeRuleResponseVo.getTotalAmount());
        //预估里程
        orderInfoForm.setExpectDistance(feeRuleResponseVo.getTotalAmount());

        //保存订单
        Long orderId = orderInfoFeignClient.addOrderInfo(orderInfoForm).getData();

        //开启定时任务等待配送员接单 TODO
        NewOrderTaskVo newOrderTaskVo = new NewOrderTaskVo();

        BeanUtils.copyProperties(orderInfoForm, newOrderTaskVo);

        newOrderTaskVo.setOrderId(orderId);
        newOrderTaskVo.setExpectTime(drivingLineVo.getDuration());//预估时间
        newOrderTaskVo.setCreateTime(new Date());

        //调用下游服务开启定时任务
        Long jobId = newOrderFeignClient.addAndStartTask(newOrderTaskVo).getData();


        return orderId;
    }


    /**
     * 查询订单状态
     */
    @Override
    public Integer queryOrderStatus(Long orderId) {
        return orderInfoFeignClient.getOrderStatus(orderId).getData();
    }


    /**
     * 顾客查询当前是否有进行中的订单
     */
    @Override
    public CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId) {
        return orderInfoFeignClient.searchCustomerCurrentOrder(customerId).getData();
    }

    /**
     * 根据订单id查询订单信息
     */
    @Override
    public OrderInfoVo getOrderInfo(Long orderId, Long customerId) {
        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderId).getData();

        //防止数据泄露  别人仅知道订单号，不能查询订单信息
        if (orderInfo.getCustomerId().longValue() != customerId.longValue()) {
            throw new PowerException(ResultCodeEnum.ILLEGAL_REQUEST);
        }

        //封装订单信息
        OrderInfoVo orderInfoVo = new OrderInfoVo();
        BeanUtils.copyProperties(orderInfo, orderInfoVo);
        orderInfoVo.setOrderId(orderId);

        return orderInfoVo;
    }


    /**
     * 查看配送员的基本信息
     * @param orderId
     * @param customerId
     * @return
     */
    @Override
    public DriverInfoVo getDriverInfo(Long orderId, Long customerId) {
        //查询订单信息  从这里获取配送员的 id
        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderId).getData();

        if (orderInfo.getCustomerId().longValue() != customerId.longValue()) {
            throw new PowerException(ResultCodeEnum.ILLEGAL_REQUEST);
        }

        //根据配送员id查询配送员信息
        return driverInfoFeignClient.getDriverInfoOrder(orderInfo.getDriverId()).getData();
    }



    @Override
    public OrderLocationVo getCacheOrderLocation(Long orderId) {
       return locationFeignClient.getCacheOrderLocation(orderId).getData();
    }


    @Override
    public DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm) {
        return mapFeignClient.calculateDrivingLine(calculateDrivingLineForm).getData();
    }

    /**
     * 查看配送员最后的位置信息
     */
    @Override
    public OrderServiceLastLocationVo getOrderServiceLastLocation(Long orderId) {
        return locationFeignClient.getOrderServiceLastLocation(orderId).getData();
    }

}
