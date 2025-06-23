package com.powernode.driver.service;

import com.powernode.model.vo.driver.DriverAuthInfoVo;
import com.powernode.model.vo.driver.DriverLoginVo;

public interface DriverService {


    String login(String code);

    DriverLoginVo getDriverLoginVo(Long driverId);

    DriverAuthInfoVo getDriverAuthInfo(Long driverId);
}
