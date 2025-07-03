package com.powernode.customer.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.result.Result;
import com.powernode.common.util.AuthContextHolder;
import com.powernode.customer.service.OrderService;
import com.powernode.model.form.customer.ExpectOrderForm;
import com.powernode.model.form.customer.SubmitOrderForm;
import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.vo.customer.ExpectOrderVo;
import com.powernode.model.vo.driver.DriverInfoVo;
import com.powernode.model.vo.map.DrivingLineVo;
import com.powernode.model.vo.map.OrderLocationVo;
import com.powernode.model.vo.map.OrderServiceLastLocationVo;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.model.vo.order.OrderInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderController {

    @Resource
    private OrderService orderService;

    @Operation(summary = "预估订单")
    @PostMapping("/expectOrder")
    public Result<ExpectOrderVo> expectOrder(@RequestBody ExpectOrderForm expectOrderForm) {
        return Result.ok(orderService.expectOrder(expectOrderForm));
    }

    @Operation(summary = "添加订单")
    @PowerLogin
    @PostMapping("/submitOrder")
    public Result<Long> submitOrder(@RequestBody SubmitOrderForm submitOrderForm) {

        Long userId = AuthContextHolder.getUserId();
        submitOrderForm.setCustomerId(userId);

        return Result.ok(orderService.addOrder(submitOrderForm));
    }

    @Operation(summary = "查询订单状态")
    @GetMapping("/getOrderStatus/{orderId}")
    public Result<Integer> getOrderStatus(@PathVariable Long orderId) {
        return Result.ok(orderService.queryOrderStatus(orderId));
    }


    @Operation(summary = "查找当前进行中订单")
    @PowerLogin
    @GetMapping("/searchCustomerCurrentOrder")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder() {
        Long customerId = AuthContextHolder.getUserId();

        return Result.ok(orderService.searchCustomerCurrentOrder(customerId));
    }


    @Operation(summary = "获取订单信息")
    @PowerLogin
    @GetMapping("/getOrderInfo/{orderId}")
    public Result<OrderInfoVo> getOrderInfo(@PathVariable Long orderId) {
        Long customerId = AuthContextHolder.getUserId();

        return Result.ok(orderService.getOrderInfo(orderId, customerId));
    }


    @Operation(summary = "获取配送员信息")
    @PowerLogin
    @GetMapping("/getDriverInfo/{orderId}")
    public Result<DriverInfoVo> getDriverInfo(@PathVariable Long orderId) {
        return Result.ok(orderService.getDriverInfo(orderId, AuthContextHolder.getUserId()));
    }


    @Operation(summary = "获取订单位置信息")
    @GetMapping("/getCacheOrderLocation/{orderId}")
    public Result<OrderLocationVo> getCacheOrderLocation(@PathVariable Long orderId) {
        return Result.ok(orderService.getCacheOrderLocation(orderId));
    }

    @Operation(summary = "获取最佳路线")
    @PowerLogin
    @PostMapping("/calculateDrivingLine")
    public Result<DrivingLineVo> calculateDrivingLine(@RequestBody CalculateDrivingLineForm calculateDrivingLineForm) {
        return Result.ok(orderService.calculateDrivingLine(calculateDrivingLineForm));
    }


    @Operation(summary = "获取配送员最后的位置信息")
    @GetMapping("/getOrderServiceLastLocation/{orderId}")
    public Result<OrderServiceLastLocationVo> getOrderServiceLastLocation(@PathVariable Long orderId) {
        return Result.ok(orderService.getOrderServiceLastLocation(orderId));
    }
}

