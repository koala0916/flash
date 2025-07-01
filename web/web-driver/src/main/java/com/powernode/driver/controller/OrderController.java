package com.powernode.driver.controller;

import com.powernode.common.annotation.PowerLogin;
import com.powernode.common.result.Result;
import com.powernode.common.util.AuthContextHolder;
import com.powernode.driver.service.OrderService;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import com.powernode.model.vo.order.NewOrderDataVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "订单API接口管理")
@RestController
@RequestMapping("/order")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderController {

    @Resource
    private OrderService orderService;



    @Operation(summary = "配送员查询当前进行中的订单")
    @GetMapping("getOrderStatus/{orderId}")
    public Result<Integer> queryOrder(@PathVariable Long orderId) {
        return Result.ok(orderService.queryOrderStatus(orderId));
    }

    @Operation(summary = "配送员查询符合的订单")
    @GetMapping("findNewOrderQueueData")
    @PowerLogin
    public Result<List<NewOrderDataVo>> findNewOrderQueueData() {
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.findNewOrderQueueData(driverId));
    }


    @Operation(summary = "查看当前配送员是否有配送中的订单")
    @PowerLogin
    @GetMapping("/searchDriverCurrentOrder")
    public Result<CurrentOrderInfoVo> searchDriverCurrentOrder() {
        Long driverId = AuthContextHolder.getUserId();
        return Result.ok(orderService.searchDriverCurrentOrder(driverId));
    }

    @Operation(summary = "配送员抢单")
    @PowerLogin
    @GetMapping("/robNewOrder/{orderId}")
    public Result<Boolean> robNewOrder(@PathVariable Long orderId) {
        Long userId = AuthContextHolder.getUserId();

        return Result.ok(orderService.robNewOrder(userId, orderId));
    }



}

