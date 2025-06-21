package com.powernode.customer.service;

import com.powernode.model.vo.customer.CustomerLoginVo;

public interface CustomerService {


    String login(String code);

    CustomerLoginVo getCustomerLoginInfo(Long customerId);
}
