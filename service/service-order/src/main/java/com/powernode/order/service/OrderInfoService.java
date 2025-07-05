package com.powernode.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.model.form.order.StartDriveForm;
import com.powernode.model.form.order.UpdateOrderBillForm;
import com.powernode.model.form.order.UpdateOrderCartForm;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import org.springframework.transaction.annotation.Transactional;

public interface OrderInfoService extends IService<OrderInfo> {

    Long addOrderInfo(OrderInfoForm orderInfoForm);

    Integer queryOrderStatus(Long orderId);

    @Transactional
    Boolean robNewOrder(Long driverId, Long orderId);

    CurrentOrderInfoVo searchCustomerCurrentOrder(Long customerId);

    CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId);

    Boolean driverArrivedStartLocation(Long orderId, Long driverId);

    Boolean updateDriverCarInfo(UpdateOrderCartForm updateOrderCartForm);

    Boolean startDrive(StartDriveForm startDriveForm);

    @Transactional(rollbackFor = Exception.class)
    Boolean driverArriveStartLocation(Long orderId, Long driverId);

    Long getOrderNumByTime(String startTime, String endTime);

    Boolean endDrive(UpdateOrderBillForm updateOrderBillForm);
}
