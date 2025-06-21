package com.powernode.driver.service;

import com.powernode.model.vo.driver.DriverLoginVo;

public interface DriverService {


    String login(String code);

    DriverLoginVo getDriverLoginVo(Long driverId);
}
