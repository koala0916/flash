package com.powernode.driver.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.driver.mapper.DriverInfoMapper;
import com.powernode.driver.service.DriverInfoService;
import com.powernode.model.entity.driver.DriverInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverInfoServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo> implements DriverInfoService {



}