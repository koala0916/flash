package com.powernode.order.controller;



import com.powernode.common.result.Result;
import com.powernode.model.entity.order.OrderMonitor;
import com.powernode.model.entity.order.OrderMonitorRecord;
import com.powernode.order.service.OrderMonitorService;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order/monitor")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderMonitorController {

    @Resource
    private OrderMonitorService orderMonitorService;


    @Operation(summary = "保存订单监控记录数据")
    @PostMapping("/saveOrderMonitorRecord")
    public Result<Boolean> saveMonitorRecord(@RequestBody OrderMonitorRecord orderMonitorRecord) {
        return Result.ok(orderMonitorService.saveOrderMonitorRecord(orderMonitorRecord));
    }


    @Operation(summary = "添加订单监控信息")
    @GetMapping("/saveOrderMonitor")
    public Result<Long> saveOrderMonitor(@PathVariable OrderMonitor orderMonitor) {
        return Result.ok(orderMonitorService.addOrderMonitor(orderMonitor));
    }

    @Operation(summary = "根据订单id获取订单监控信息")
    @GetMapping("/getOrderMonitor/{orderId}")
    public Result<OrderMonitor> getOrderMonitor(@PathVariable Long orderId) {
        return Result.ok(orderMonitorService.getOrderMonitor(orderId));
    }

    @Operation(summary = "更新订单监控信息")
    @PostMapping("/updateOrderMonitor")
    public Result<Boolean> updateOrderMonitor(@RequestBody OrderMonitor OrderMonitor) {
        return Result.ok(orderMonitorService.updateOrderMonitor(OrderMonitor));
    }
}

