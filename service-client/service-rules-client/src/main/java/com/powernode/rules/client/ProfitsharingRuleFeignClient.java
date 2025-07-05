package com.powernode.rules.client;

import com.powernode.common.result.Result;
import com.powernode.model.form.rules.ProfitsharingRuleRequestForm;
import com.powernode.model.vo.rules.ProfitsharingRuleResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-rules")
public interface ProfitsharingRuleFeignClient {

    @PostMapping("/rules/profitsharing/calculateOrderProfitsharingFee")
    Result<ProfitsharingRuleResponseVo> calculateOrderProfitsharingFee(@RequestBody ProfitsharingRuleRequestForm profitsharingRuleRequestForm);
}