package com.powernode.driver.service.impl;


import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.driver.service.DriverService;
import com.powernode.model.vo.driver.DriverAuthInfoVo;
import com.powernode.model.vo.driver.DriverLoginVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import com.powernode.common.constant.RedisConstant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverServiceImpl implements DriverService {

    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String login(String code) {

        // 调用服务 获取配送员主键
        Long driverId = driverInfoFeignClient.login(code).getData();

        // 生成token
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        stringRedisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX + token,driverId.toString(),RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.SECONDS);

        return token;
    }


    /**
     * 获取配送员登录信息
     * @param driverId
     * @return
     */
    @Override
    public DriverLoginVo getDriverLoginVo(Long driverId) {
       return driverInfoFeignClient.getDriverLoginInfo(driverId).getData();
    }


    /**
     * 获取配送员认证信息
     */
    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {
        return driverInfoFeignClient.getDriverAuthInfo(driverId).getData();
    }
}
