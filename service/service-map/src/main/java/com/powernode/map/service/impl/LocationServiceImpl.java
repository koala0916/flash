package com.powernode.map.service.impl;


import com.powernode.common.constant.RedisConstant;
import com.powernode.map.service.LocationService;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 将配送员的位置信息存储到redis数据库中
     */
    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {

        //将配送员的信息存入redis  Point是spring的包下
        Point point = new Point(updateDriverLocationForm.getLongitude().doubleValue(), updateDriverLocationForm.getLatitude().doubleValue());

        //这里使用的是opsForGeo
        redisTemplate.opsForGeo().add(RedisConstant.DRIVER_GEO_LOCATION,point, updateDriverLocationForm.getDriverId());

        return true;
    }


    /**
     * 删除配送员的位置信息  下班了
     */
    @Override
    public Boolean removeDriverLocation(Long driverId) {
        redisTemplate.opsForGeo().remove(RedisConstant.DRIVER_GEO_LOCATION, driverId);
        return true;
    }
}
