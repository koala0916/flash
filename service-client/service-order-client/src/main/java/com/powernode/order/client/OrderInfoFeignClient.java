package com.powernode.order.client;

import com.powernode.common.result.Result;
import com.powernode.model.form.order.OrderInfoForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "service-order")
public interface OrderInfoFeignClient {

    @PostMapping("/order/info/addOrderInfo")
    Result<Long> addOrderInfo(@RequestBody OrderInfoForm orderInfoForm);
}