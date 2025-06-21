package com.powernode.driver.service.impl;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.common.constant.SystemConstant;
import com.powernode.driver.mapper.DriverAccountMapper;
import com.powernode.driver.mapper.DriverInfoMapper;
import com.powernode.driver.mapper.DriverLoginLogMapper;
import com.powernode.driver.mapper.DriverSetMapper;
import com.powernode.driver.service.DriverInfoService;
import com.powernode.model.entity.driver.DriverAccount;
import com.powernode.model.entity.driver.DriverInfo;
import com.powernode.model.entity.driver.DriverLoginLog;
import com.powernode.model.entity.driver.DriverSet;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverInfoServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo> implements DriverInfoService {

    @Resource
    private WxMaService wxMaService;

    @Resource
    private DriverSetMapper driverSetMapper;

    @Resource
    private DriverAccountMapper driverAccountMapper;

    @Resource
    private DriverLoginLogMapper driverLoginLogMapper;

    @Transactional
    @Override
    public Long login(String code)  {
        try {
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);

            String openid = sessionInfo.getOpenid();
            //获取配送员的信息
            DriverInfo driverInfo = getOne(new LambdaQueryWrapper<DriverInfo>().eq(DriverInfo::getWxOpenId, openid));

            if (driverInfo == null) {
                //说明用户第一次登录，创建用户
                driverInfo = new DriverInfo();

                driverInfo.setAvatarUrl("https://img.phb123.com/uploads/allimg/220805/810-220P51012170-L.png");
                driverInfo.setNickname("动力节点");
                driverInfo.setWxOpenId(openid);
                this.save(driverInfo);


                //配送员端登录后接单状态默认设置
                DriverSet driverSet = new DriverSet();
                driverSet.setDriverId(driverInfo.getId());
                driverSet.setOrderDistance(new BigDecimal(0));
                driverSet.setAcceptDistance(new BigDecimal(SystemConstant.ACCEPT_DISTANCE));
                driverSet.setIsAutoAccept(0);
                driverSetMapper.insert(driverSet);

                //创建配送员默认的账户金额数据
                DriverAccount driverAccount = new DriverAccount();
                driverAccount.setDriverId(driverInfo.getId());
                driverAccountMapper.insert(driverAccount);
            }

            //创建登录日志信息
            DriverLoginLog driverLoginLog = new DriverLoginLog();
            driverLoginLog.setDriverId(driverInfo.getId());
            driverLoginLog.setMsg("微信小程序");
            driverLoginLogMapper.insert(driverLoginLog);

            return driverInfo.getId();

        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

}