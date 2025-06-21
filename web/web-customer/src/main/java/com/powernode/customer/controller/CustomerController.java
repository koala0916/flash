package com.powernode.customer.controller;

import com.powernode.common.constant.RedisConstant;
import com.powernode.common.result.Result;
import com.powernode.customer.service.CustomerService;
import com.powernode.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "客户API接口管理")
@RestController
@RequestMapping("/customer")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerController {

    @Resource
    private CustomerService customerService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "小程序登录")
    @GetMapping("/login/{code}")
    public Result<String> login(@PathVariable String code) {
        return Result.ok(customerService.login(code));
    }



    @Operation(summary = "获取客户登录信息")
    @GetMapping("/getCustomerLoginInfo")
    public Result<CustomerLoginVo> getCustomerLoginInfo(@RequestHeader("token") String token) {

        String customerId = stringRedisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);

        return Result.ok(customerService.getCustomerLoginInfo(Long.parseLong(customerId)));
    }
}

