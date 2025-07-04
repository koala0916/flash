package com.powernode.order.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.model.entity.order.OrderMonitor;
import com.powernode.model.entity.order.OrderMonitorRecord;
import com.powernode.order.mapper.OrderMonitorMapper;
import com.powernode.order.repository.OrderMonitorRecordRepository;
import com.powernode.order.service.OrderMonitorService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderMonitorServiceImpl extends ServiceImpl<OrderMonitorMapper, OrderMonitor> implements OrderMonitorService {

    @Resource
    private OrderMonitorRecordRepository orderMonitorRecordRepository;


    /**
     * 将minio中的音频地址和转换后的文字存储到mongodb中
     */
    @Override
    public Boolean saveOrderMonitorRecord(OrderMonitorRecord orderMonitorRecord) {
        orderMonitorRecordRepository.save(orderMonitorRecord);
        return true;
    }

    /**
     * 根据订单ID查询订单监控信息
     */
    @Override
    public OrderMonitor getOrderMonitor(Long orderId) {
        LambdaQueryWrapper<OrderMonitor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderMonitor::getOrderId, orderId);

        return getOne(queryWrapper);
    }

    @Override
    public Boolean updateOrderMonitor(OrderMonitor orderMonitor) {
        return updateById(orderMonitor);
    }

    @Override
    public Long addOrderMonitor(OrderMonitor orderMonitor) {
        save(orderMonitor);
        return orderMonitor.getId();
    }
}
