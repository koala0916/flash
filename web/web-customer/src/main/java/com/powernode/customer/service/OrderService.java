package com.powernode.customer.service;

import com.powernode.model.form.customer.ExpectOrderForm;
import com.powernode.model.vo.customer.ExpectOrderVo;

public interface OrderService {

    ExpectOrderVo expectOrder(ExpectOrderForm exerciseOrderForm);
}
