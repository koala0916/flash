package com.powernode.order.client;

import com.powernode.common.result.Result;
import com.powernode.model.entity.order.OrderInfo;
import com.powernode.model.form.order.OrderInfoForm;
import com.powernode.model.form.order.StartDriveForm;
import com.powernode.model.form.order.UpdateOrderCartForm;
import com.powernode.model.vo.order.CurrentOrderInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "service-order")
public interface OrderInfoFeignClient {

    @PostMapping("/order/info/addOrderInfo")
    Result<Long> addOrderInfo(@RequestBody OrderInfoForm orderInfoForm);

    @GetMapping("/order/info/getOrderStatus/{orderId}")
    Result<Integer> getOrderStatus(@PathVariable Long orderId);

    @GetMapping("/order/info/robNewOrder/{driverId}/{orderId}")
    Result<Boolean> robNewOrder(@PathVariable Long driverId, @PathVariable Long orderId);

    @GetMapping("/order/info/searchCustomerCurrentOrder/{customerId}")
    Result<CurrentOrderInfoVo> searchCustomerCurrentOrder(@PathVariable Long customerId);

    @GetMapping("/order/info/searchDriverCurrentOrder/{driverId}")
    Result<CurrentOrderInfoVo> searchDriverCurrentOrder(@PathVariable Long driverId);

    @GetMapping("/order/info/getOrderInfo/{orderId}")
    Result<OrderInfo> getOrderInfo(@PathVariable Long orderId);

    @GetMapping("/order/info/driverArrivedStartLocation/{orderId}/{driverId}")
    Result<Boolean> driverArrivedStartLocation(@PathVariable Long orderId, @PathVariable Long driverId);

    @PostMapping("/order/info/updateOrderCart")
    Result<Boolean> updateOrderCart(@RequestBody UpdateOrderCartForm updateOrderCartForm);

    @PostMapping("/order/info/startDrive")
    Result<Boolean> startDrive(@RequestBody StartDriveForm startDriveForm);

    /**
     * 配送员到达起始点
     */
    @GetMapping("/order/info/driverArriveStartLocation/{orderId}/{driverId}")
    Result<Boolean> driverArriveStartLocation(@PathVariable("orderId") Long orderId, @PathVariable("driverId") Long driverId);


    @GetMapping("/order/info/getOrderNumByTime/{startTime}/{endTime}")
    Result<Long> getOrderNumByTime(@PathVariable String startTime, @PathVariable String endTime);
}