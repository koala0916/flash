package com.powernode.customer.service.impl;


import com.powernode.common.constant.RedisConstant;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.Result;
import com.powernode.customer.client.CustomerInfoFeignClient;
import com.powernode.customer.service.CustomerService;
import com.powernode.model.form.customer.UpdateWxPhoneForm;
import com.powernode.model.vo.customer.CustomerLoginVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerServiceImpl implements CustomerService {


    @Resource
    private CustomerInfoFeignClient customerInfoFeignClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 1.获取小程序的code，传入下游服务
     * 2.生成token 存入redis
     * 3.返回token给小程序
     * @param code
     * @return
     */
    @Override
    public String login(String code){
        //1.获取小程序的code，传入下游服务
        Result<Long> result = customerInfoFeignClient.login(code);

        if (result.getCode().intValue() != 200) {
            throw new PowerException(result.getCode(), result.getMessage());
        }

        // 2.生成token 存入redis
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        stringRedisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX + token,result.getData().toString(),RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.SECONDS);

        //3.返回token给小程序
        return token;
    }


    @Override
    public CustomerLoginVo getCustomerLoginInfo(Long customerId){
        Result<CustomerLoginVo> result = customerInfoFeignClient.getCustomerLoginInfo(customerId);

//        if (result.getCode().intValue() != 200) {
//            throw new PowerException(result.getCode(), result.getMessage());
//        }

        return result.getData();
    }

    @Override
    public Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm){

        Result<Boolean> booleanResult = customerInfoFeignClient.updateWxPhoneNumber(updateWxPhoneForm);

//        if (booleanResult.getCode().intValue() != 200) {
//            throw new PowerException(booleanResult.getCode(), booleanResult.getMessage());
//        }

        return booleanResult.getData();
    }
}
