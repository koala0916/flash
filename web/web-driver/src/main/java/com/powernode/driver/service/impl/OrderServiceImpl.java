package com.powernode.driver.service.impl;


import com.powernode.driver.service.OrderService;
import com.powernode.order.client.OrderInfoFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    @Override
    public Integer queryOrderStatus(Long orderId) {
        return orderInfoFeignClient.getOrderStatus(orderId).getData();
    }
}
