package com.powernode.map.service.impl;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.powernode.common.constant.RedisConstant;
import com.powernode.common.constant.SystemConstant;
import com.powernode.driver.client.DriverInfoFeignClient;
import com.powernode.map.service.LocationService;
import com.powernode.model.entity.driver.DriverSet;
import com.powernode.model.form.map.SearchNearByDriverForm;
import com.powernode.model.form.map.UpdateDriverLocationForm;
import com.powernode.model.form.map.UpdateOrderLocationForm;
import com.powernode.model.vo.map.NearByDriverVo;
import com.powernode.model.vo.map.OrderLocationVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DriverInfoFeignClient driverInfoFeignClient;

    /**
     * 将配送员的位置信息存储到redis数据库中
     */
    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {

        //将配送员的信息存入redis  Point是spring的包下
        Point point = new Point(updateDriverLocationForm.getLongitude().doubleValue(), updateDriverLocationForm.getLatitude().doubleValue());

        //这里使用的是opsForGeo
        redisTemplate.opsForGeo().add(RedisConstant.DRIVER_GEO_LOCATION,point, updateDriverLocationForm.getDriverId()+"");

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


    /**
     * 搜索附近合适的配送员
     */
    @Override
    public List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm) {

        //查询附近5公里范围内的配送员
        Point point = new Point(searchNearByDriverForm.getLongitude().doubleValue(), searchNearByDriverForm.getLatitude().doubleValue());

        //定义距离 5公里
        Distance distance = new Distance(SystemConstant.NEARBY_DRIVER_RADIUS, RedisGeoCommands.DistanceUnit.KILOMETERS);

        //以point为中心点 距离为5公里 画圆
        Circle circle = new Circle(point, distance);

        //构建geo参数
        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands
                        .GeoRadiusCommandArgs.newGeoRadiusArgs().
                        includeCoordinates()  //包含坐标
                        .includeDistance()   //距离
                        .sortAscending();   // 排序 升序

        //redis搜索信息、
        GeoResults<RedisGeoCommands.GeoLocation<String>> result = redisTemplate.opsForGeo().radius(RedisConstant.DRIVER_GEO_LOCATION, circle, args);

        //获取信息
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = result.getContent();

        //保存符合派单条件的配送员
        List<NearByDriverVo> list = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(content)){
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<String>>> iterator = content.iterator();

            while (iterator.hasNext()) {
                GeoResult<RedisGeoCommands.GeoLocation<String>> item = iterator.next();

                //获取配送员的id  更新配送员位置的时候，存储了经纬度和配送员的id，这里的getName获取的就是配送员的id
                String name = item.getContent().getName();
                long driverId = Long.parseLong(name);

                //距离
                BigDecimal currentDistance = new BigDecimal(item.getDistance().getValue()).setScale(2, BigDecimal.ROUND_HALF_UP);


                //获取配送员个性化参数
                DriverSet driverSet = driverInfoFeignClient.getDriverSet(driverId).getData();
                //判断当前订单是否符合配送员的个性化设置
                if (driverSet.getAcceptDistance().doubleValue() != 0 && driverSet.getAcceptDistance().subtract(currentDistance).doubleValue() < 0) {
                    continue;
                }

                //搜索满足条件的配送员
                NearByDriverVo nearByDriverVo = new NearByDriverVo();
                nearByDriverVo.setDriverId(driverId);
                nearByDriverVo.setDistance(currentDistance);
                list.add(nearByDriverVo);

            }
        }

        return list;
    }


    /**
     * 位置同显 更新配送员位置信息
     * @param updateOrderLocationForm
     * @return
     */
    @Override
    public Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm) {
        OrderLocationVo orderLocationVo = new OrderLocationVo();
        orderLocationVo.setLatitude(updateOrderLocationForm.getLatitude());
        orderLocationVo.setLongitude(updateOrderLocationForm.getLongitude());

        //将位置数据放入redis
        redisTemplate.opsForValue().set(RedisConstant.UPDATE_ORDER_LOCATION+updateOrderLocationForm.getOrderId(), orderLocationVo);

        return true;
    }


    /*
        获取订单起始位置信息
     */
    @Override
    public OrderLocationVo getCacheOrderLocation(Long orderId) {
        return (OrderLocationVo) redisTemplate.opsForValue().get(RedisConstant.UPDATE_ORDER_LOCATION+orderId);
    }
}
