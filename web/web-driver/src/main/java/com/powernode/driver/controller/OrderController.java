package com.powernode.driver.controller;

import com.powernode.common.result.Result;
import com.powernode.driver.service.OrderService;
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


    @Operation(summary = "配送员查询当前进行中的订单")
    @GetMapping("getOrderStatus/{orderId}")
    public Result<Integer> queryOrder(@PathVariable Long orderId) {
        return Result.ok(orderService.queryOrderStatus(orderId));
    }
}

