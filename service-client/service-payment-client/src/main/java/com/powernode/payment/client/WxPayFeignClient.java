package com.powernode.payment.client;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(value = "service-payment")
public interface WxPayFeignClient {


}