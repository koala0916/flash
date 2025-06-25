package com.powernode.order.controller;

import com.powernode.common.result.Result;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.order.service.OrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@Tag(name = "订单API接口管理")
@RestController
@RequestMapping(value = "/order/info")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderInfoController {

    @Resource
    private OrderInfoService orderInfoService;

    @Operation(summary = "添加订单")
    @PostMapping("/addOrderInfo")
    public Result<Long> addOrderInfo(@RequestBody OrderInfoForm orderInfoForm) {
        return Result.ok(orderInfoService.addOrderInfo(orderInfoForm));
    }


    /**
     * 查询订单状态
     */
    @Operation(summary = "查询订单状态")
    @GetMapping("/getOrderStatus/{orderId}")
    public Result<Integer> getOrderStatus(@PathVariable Long orderId) {
        return Result.ok(orderInfoService.queryOrderStatus(orderId));
    }
}

