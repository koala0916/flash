package com.powernode.driver.service;

import com.powernode.model.entity.driver.DriverInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

public interface DriverInfoService extends IService<DriverInfo> {

    @Transactional
    Long login(String code);
}
