package com.powernode.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.model.entity.order.OrderMonitor;
import com.powernode.model.entity.order.OrderMonitorRecord;

public interface OrderMonitorService extends IService<OrderMonitor> {

    Boolean saveOrderMonitorRecord(OrderMonitorRecord orderMonitorRecord);

    OrderMonitor getOrderMonitor(Long orderId);

    Boolean updateOrderMonitor(OrderMonitor orderMonitor);

    Long addOrderMonitor(OrderMonitor orderMonitor);
}
