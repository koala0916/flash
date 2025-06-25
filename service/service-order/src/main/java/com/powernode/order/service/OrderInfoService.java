package com.powernode.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.form.order.OrderInfoForm;

public interface OrderInfoService extends IService<OrderInfo> {

    Long addOrderInfo(OrderInfoForm orderInfoForm);

    Integer queryOrderStatus(Long orderId);
}
