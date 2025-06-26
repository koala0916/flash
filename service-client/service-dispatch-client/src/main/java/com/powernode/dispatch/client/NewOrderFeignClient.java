package com.powernode.dispatch.client;

import com.powernode.common.result.Result;
import com.powernode.model.vo.dispatch.NewOrderTaskVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "service-dispatch")
public interface NewOrderFeignClient {

    @PostMapping("/dispatch/newOrder/addAndStartTask")
    Result<Long> addAndStartTask(@RequestBody NewOrderTaskVo newOrderTaskVo);
}