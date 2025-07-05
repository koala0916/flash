package com.powernode.rules.service;

import com.powernode.model.form.rules.RewardRuleRequestForm;
import com.powernode.model.vo.rules.RewardRuleResponseVo;

public interface RewardRuleService {

    RewardRuleResponseVo calculateOrderRewardFee(RewardRuleRequestForm rewardRuleRequestForm);
}
