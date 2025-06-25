package com.powernode.order.controller;

import com.powernode.common.result.Result;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.order.service.OrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
}

