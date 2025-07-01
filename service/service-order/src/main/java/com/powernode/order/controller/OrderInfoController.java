package com.powernode.order.controller;

import com.powernode.common.result.Result;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.model.form.order.UpdateOrderCartForm;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
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


    /**
     * 配送员抢单
     */
    @Operation(summary = "配送员抢单")
    @GetMapping("/robNewOrder/{driverId}/{orderId}")
    public Result<Boolean> robNewOrder(@PathVariable Long driverId, @PathVariable Long orderId) {
        return Result.ok(orderInfoService.robNewOrder(driverId, orderId));
    }

    @Operation(summary = "查询当前订单信息")
    @GetMapping("/searchCustomerCurrentOrder/{customerId}")
    public Result<CurrentOrderInfoVo> searchCustomerCurrentOrder(@PathVariable Long customerId) {
        return Result.ok(orderInfoService.searchCustomerCurrentOrder(customerId));
    }

    @Operation(summary = "查询当前进行中的")
    @GetMapping("/searchDriverCurrentOrder/{driverId}")
    public Result<CurrentOrderInfoVo> searchDriverCurrentOrder(@PathVariable Long driverId) {
        return Result.ok(orderInfoService.searchDriverCurrentOrder(driverId));
    }

    @Operation(summary = "根据订单id查看订单信息")
    @GetMapping("/getOrderInfo/{orderId}")
    public Result<OrderInfo> getOrderInfo(@PathVariable Long orderId) {
        return Result.ok(orderInfoService.getById(orderId));
    }


    @Operation(summary = "配送员到达指定位置")
    @GetMapping("/driverArrivedStartLocation/{orderId}/{driverId}")
    public Result<Boolean> driverArrivedStartLocation(@PathVariable Long orderId, @PathVariable Long driverId) {
        return Result.ok(orderInfoService.driverArrivedStartLocation(orderId, driverId));
    }

    @Operation(summary = "更新配送员车辆信息")
    @PostMapping("/updateOrderCart")
    public Result<Boolean> updateOrderCart(@RequestBody UpdateOrderCartForm updateOrderCartForm) {
        return Result.ok(orderInfoService.updateDriverCarInfo(updateOrderCartForm));
    }
}

