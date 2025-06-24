package com.powernode.map.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.powernode.common.execption.PowerException;
import com.powernode.common.result.ResultCodeEnum;
import com.powernode.map.service.MapService;
import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.vo.map.DrivingLineVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class MapServiceImpl implements MapService {


    @Resource
    private RestTemplate restTemplate;

    @Value("${tencent.map.key}")
    private String key;

    /**
     * 根据经纬度计算路线
     */
    @Override
    public DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm) {
        String url = "https://apis.map.qq.com/ws/direction/v1/driving/?from={from}&to={to}&key={key}";

        //构建参数
        HashMap<String, String> map = new HashMap<>();
        map.put("from",calculateDrivingLineForm.getStartPointLatitude() +"," + calculateDrivingLineForm.getStartPointLongitude());
        map.put("to",calculateDrivingLineForm.getEndPointLatitude() +"," + calculateDrivingLineForm.getEndPointLongitude());
        map.put("key", key);

        //发送请求
        JSONObject result = restTemplate.getForObject(url, JSONObject.class, map);

        int status = result.getIntValue("status");
        if (status != 0) {
            log.error("地图服务异常：{}", status);
            throw new PowerException(ResultCodeEnum.MAP_FAIL);
        }

        //获取第一条最佳路线
        JSONObject route = result.getJSONObject("result")
                .getJSONArray("routes")
                .getJSONObject(0);

        DrivingLineVo drivingLineVo = new DrivingLineVo();

        //获取总距离，单位 米
        BigDecimal distance = route.getBigDecimal("distance");

        //换算单位为千米
        BigDecimal km = distance.divide(new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_HALF_UP);

        //估算距离 千米
        drivingLineVo.setDistance(km);

        //估算时间 分钟
        drivingLineVo.setDuration(route.getBigDecimal("duration"));

        //标点串
        drivingLineVo.setPolyline(route.getJSONArray("polyline"));


        return drivingLineVo;
    }
}
