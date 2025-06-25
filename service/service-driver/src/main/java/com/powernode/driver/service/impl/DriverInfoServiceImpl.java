package com.powernode.driver.service.impl;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.common.constant.SystemConstant;
import com.powernode.driver.config.TencentProperties;
import com.powernode.driver.mapper.DriverAccountMapper;
import com.powernode.driver.mapper.DriverInfoMapper;
import com.powernode.driver.mapper.DriverLoginLogMapper;
import com.powernode.driver.mapper.DriverSetMapper;
import com.powernode.driver.service.CosService;
import com.powernode.driver.service.DriverInfoService;
import com.powernode.model.entity.driver.DriverAccount;
import com.powernode.model.entity.driver.DriverInfo;
import com.powernode.model.entity.driver.DriverLoginLog;
import com.powernode.model.entity.driver.DriverSet;
import com.powernode.model.form.driver.DriverFaceModelForm;
import com.powernode.model.form.driver.UpdateDriverAuthInfoForm;
import com.powernode.model.vo.driver.DriverAuthInfoVo;
import com.powernode.model.vo.driver.DriverLoginVo;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.iai.v20180301.IaiClient;
import com.tencentcloudapi.iai.v20180301.models.CreatePersonRequest;
import com.tencentcloudapi.iai.v20180301.models.CreatePersonResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
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

    @Resource
    private CosService cosService;

    @Resource
    private TencentProperties tencentProperties;

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


    /**
     * 获取配送员信息
     */

    @Override
    public DriverLoginVo getDriverLoginVo(Long driverId) {
        DriverInfo driverInfo = getById(driverId);

        DriverLoginVo driverLoginVo = new DriverLoginVo();
        BeanUtils.copyProperties(driverInfo, driverLoginVo);

        //判断是否创建人脸库
        boolean isArchiveFace = driverInfo.getFaceModelId() != null;

        driverLoginVo.setIsArchiveFace(isArchiveFace);

        return driverLoginVo;
    }

    /**
     * 查询配送员认证信息
     */
    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {
        DriverInfo driverInfo = getById(driverId);
        DriverAuthInfoVo driverAuthInfoVo = new DriverAuthInfoVo();

        BeanUtils.copyProperties(driverInfo, driverAuthInfoVo);

        driverAuthInfoVo.setDriverId(driverId);

        //获取回显地址，我们的回显地址是有过期时间的
        driverAuthInfoVo.setIdcardBackShowUrl(cosService.getImageUrl(driverInfo.getIdcardBackUrl()));
        driverAuthInfoVo.setIdcardFrontShowUrl(cosService.getImageUrl(driverInfo.getIdcardFrontUrl()));
        driverAuthInfoVo.setIdcardHandShowUrl(cosService.getImageUrl(driverInfo.getIdcardHandUrl()));
        return driverAuthInfoVo;
    }


    /**
     * 修改配送员信息
     */
    @Override
    public Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        DriverInfo driverInfo = new DriverInfo();
        BeanUtils.copyProperties(updateDriverAuthInfoForm, driverInfo);
        //设置配送员id主键
        driverInfo.setId(updateDriverAuthInfoForm.getDriverId());

        return updateById(driverInfo);
    }

    /**
     * 创建人脸库
     */
    @Override
    public Boolean createDriverFaceModel(DriverFaceModelForm driverFaceModelForm) {
        DriverInfo driverInfo = getById(driverFaceModelForm.getDriverId());

        //创建凭证
        Credential cred = new Credential(tencentProperties.getSecretId(), tencentProperties.getSecretKey());

        //构建请求对象
        CreatePersonRequest req = new CreatePersonRequest();

        req.setGroupId(tencentProperties.getPersionGroupId());
        req.setPersonId(driverInfo.getId().toString());
        req.setGender(Long.parseLong(driverInfo.getGender()));

        req.setQualityControl(4L);
        req.setUniquePersonControl(4L);
        req.setPersonName(driverInfo.getName());
        //传入人脸识别的图片base64数据
        req.setImage(driverFaceModelForm.getImageBase64());


        //发送请求
        try {
            IaiClient iaiClient = new IaiClient(cred, tencentProperties.getRegion());
            CreatePersonResponse response = iaiClient.CreatePerson(req);

            //从响应中获取faceid 将faceId存入自己的数据库中
            String faceId = response.getFaceId();

            driverInfo.setFaceModelId(faceId);

            updateById(driverInfo);

            return true;
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取配送员的个性化设置
     */
    @Override
    public DriverSet getDriverSet(Long driverId) {
        LambdaQueryWrapper<DriverSet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DriverSet::getDriverId, driverId);
        return driverSetMapper.selectOne(queryWrapper);
    }

}