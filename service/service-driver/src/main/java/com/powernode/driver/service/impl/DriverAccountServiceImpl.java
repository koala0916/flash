package com.powernode.driver.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.driver.mapper.DriverAccountMapper;
import com.powernode.driver.service.DriverAccountService;
import com.powernode.model.entity.driver.DriverAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverAccountServiceImpl extends ServiceImpl<DriverAccountMapper, DriverAccount> implements DriverAccountService {


}
