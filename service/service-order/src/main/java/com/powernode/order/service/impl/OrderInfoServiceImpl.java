package com.powernode.order.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.order.mapper.OrderInfoMapper;
import com.powernode.order.service.OrderInfoService;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {


}
