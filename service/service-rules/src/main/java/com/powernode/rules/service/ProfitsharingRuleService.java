package com.powernode.rules.service;

import com.powernode.model.form.rules.ProfitsharingRuleRequestForm;
import com.powernode.model.vo.rules.ProfitsharingRuleResponseVo;

public interface ProfitsharingRuleService {

    ProfitsharingRuleResponseVo calculateProfitsharingFee(ProfitsharingRuleRequestForm profitsharingRuleRequestForm);
}
