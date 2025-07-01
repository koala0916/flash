package com.powernode.driver.service.impl;


import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.driver.service.LocationService;
import com.powernode.map.client.LocationFeignClient;
import com.powernode.model.entity.driver.DriverSet;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import com.powernode.model.form.map.UpdateOrderLocationForm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

    @Resource
    private LocationFeignClient locationFeignClient;

    /**
     * 修改配送员的位置信息
     */
    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {

        //查看配送员是否开启接单
        DriverSet driverSet = driverInfoFeignClient.getDriverSet(updateDriverLocationForm.getDriverId()).getData();

        if (driverSet.getServiceStatus() == 1) {
            return locationFeignClient.updateDriverLocation(updateDriverLocationForm).getData();
        }

        throw new PowerException(ResultCodeEnum.NO_START_SERVICE);


    }


    /**
     * 更新配送员位置到缓存
     */
    @Override
    public Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm) {
        return locationFeignClient.updateOrderLocationToCache(updateOrderLocationForm).getData();
    }
}
