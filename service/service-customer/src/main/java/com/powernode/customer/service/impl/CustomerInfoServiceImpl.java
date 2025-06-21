package com.powernode.customer.service.impl;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.customer.mapper.CustomerInfoMapper;
import com.powernode.customer.mapper.CustomerLoginLogMapper;
import com.powernode.customer.service.CustomerInfoService;
import com.powernode.model.entity.customer.CustomerInfo;
import com.powernode.model.entity.customer.CustomerLoginLog;
import com.powernode.model.vo.customer.CustomerLoginVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {

    @Resource
    private WxMaService wxMaService;

    @Resource
    private CustomerLoginLogMapper customerLoginLogMapper;
    
    /**
     *  微信登录
     *  1.获取openid
     *  2.根据openid查询用户
     *  3.判断用户是否存在，若不存在，则创建用户
     * @param code
     * @return
     */
    @Transactional
    @Override
    public Long login(String code)  {

        try {
            //1.获取openid
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            String openid = sessionInfo.getOpenid();

            //2.根据openid查询用户
            CustomerInfo customerInfo = this.getOne(new LambdaQueryWrapper<CustomerInfo>().eq(CustomerInfo::getWxOpenId, openid));

            if (customerInfo == null) {
                //说明用户第一次登录，创建用户
                customerInfo = new CustomerInfo();

                customerInfo.setWxOpenId(openid);
                customerInfo.setNickname(String.valueOf(System.currentTimeMillis()));

                customerInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");

                save(customerInfo);
            }

            //登录日志
            CustomerLoginLog customerLoginLog = new CustomerLoginLog();
            customerLoginLog.setCustomerId(customerInfo.getId());
            customerLoginLog.setMsg("小程序登录");

            customerLoginLogMapper.insert(customerLoginLog);

            return customerInfo.getId();

        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public CustomerLoginVo getCustomerLoginInfo(Long customerId) {
        CustomerInfo customerInfo = getById(customerId);

        CustomerLoginVo customerLoginVo = new CustomerLoginVo();

        //将customerInfo中的数据复制到customerLoginVo中
        BeanUtils.copyProperties(customerInfo, customerLoginVo);

        //判断是否绑定手机号码
        boolean isBindingPhone = StringUtils.hasText(customerInfo.getPhone());

        customerLoginVo.setIsBindPhone(isBindingPhone);

        return customerLoginVo;
    }
}
