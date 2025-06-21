package com.powernode.customer.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.model.entity.customer.CustomerInfo;
import com.powernode.model.form.customer.UpdateWxPhoneForm;
import com.powernode.model.vo.customer.CustomerLoginVo;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerInfoService extends IService<CustomerInfo> {

    @Transactional
    Long login(String code);

    CustomerLoginVo getCustomerLoginInfo(Long customerId);

    Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm);
}
