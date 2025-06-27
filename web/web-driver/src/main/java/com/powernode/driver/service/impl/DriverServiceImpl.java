package com.powernode.driver.service.impl;


import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.dispatch.client.NewOrderFeignClient;
import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.driver.service.DriverService;
import com.powernode.map.client.LocationFeignClient;
import com.powernode.model.form.driver.DriverFaceModelForm;
import com.powernode.model.form.driver.UpdateDriverAuthInfoForm;
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

    @Resource
    private LocationFeignClient locationFeignClient;

    @Resource
    private NewOrderFeignClient newOrderFeignClient;

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

    /**
     * 更新配送员认证信息
     */
    @Override
    public Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        return driverInfoFeignClient.updateDriverAuthInfo(updateDriverAuthInfoForm).getData();
    }


    /**
     * 创建配送员人脸模型
     */
    @Override
    public Boolean createDriverFaceModel(DriverFaceModelForm driverFaceModel) {
        return driverInfoFeignClient.createDriverFaceModel(driverFaceModel).getData();
    }


    /**
     * 查看配送员当天是否进行人脸识别
     */
    @Override
    public Boolean isFaceRecognition(Long driverId) {
        return driverInfoFeignClient.isFaceRecognition(driverId).getData();
    }

    /**
     * 配送员人脸识别验证
     */
    @Override
    public Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm) {
        return driverInfoFeignClient.verifyDriverFace(driverFaceModelForm).getData();
    }


    /**
     * 配送员开启接单
     */
    @Override
    public Boolean startService(Long driverId) {
        //判断认证状态
        DriverLoginVo driverLoginVo = driverInfoFeignClient.getDriverLoginInfo(driverId).getData();
        if (driverLoginVo.getAuthStatus() != 2) {
            throw new PowerException(ResultCodeEnum.AUTH_ERROR);
        }

        //判断当日是否进行人脸识别
        Boolean isFaceRecognition = driverInfoFeignClient.isFaceRecognition(driverId).getData();
        if (!isFaceRecognition) {
            throw new PowerException(ResultCodeEnum.FACE_ERROR);
        }

        //更新配送员的接单状态为开始接单  1表示开始接单
        driverInfoFeignClient.updateServiceStatus(driverId, 1);

        //删除配送员的位置信息
        locationFeignClient.removeDriverLocation(driverId);

        //清空当前配送员在redis中的订单list
        newOrderFeignClient.clearNewOrderQueueData(driverId);

        return true;

    }


    /**
     * 配送员停止接单
     * @param driverId
     * @return
     */
    @Override
    public Boolean stopService(Long driverId) {
        driverInfoFeignClient.updateServiceStatus(driverId, 0);

        //删除配送员的位置信息
        locationFeignClient.removeDriverLocation(driverId);

        //清空当前配送员在redis中的订单list
        newOrderFeignClient.clearNewOrderQueueData(driverId);

        return true;

    }
}
