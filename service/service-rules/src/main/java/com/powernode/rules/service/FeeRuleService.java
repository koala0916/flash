package com.powernode.rules.service;

import com.powernode.model.form.rules.FeeRuleRequestForm;
import com.powernode.model.vo.rules.FeeRuleResponseVo;

public interface FeeRuleService {

    FeeRuleResponseVo calculateOrderFee(FeeRuleRequestForm feeRuleRequestForm);
}
