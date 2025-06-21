package com.powernode.driver.service;

import com.powernode.model.entity.driver.DriverInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.model.vo.driver.DriverLoginVo;
import org.springframework.transaction.annotation.Transactional;

public interface DriverInfoService extends IService<DriverInfo> {

    @Transactional
    Long login(String code);

    DriverLoginVo getDriverLoginVo(Long driverId);
}
