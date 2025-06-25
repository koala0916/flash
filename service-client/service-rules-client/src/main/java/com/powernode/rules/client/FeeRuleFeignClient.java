package com.powernode.rules.client;

import com.powernode.common.result.Result;
import com.powernode.model.form.rules.FeeRuleRequestForm;
import com.powernode.model.vo.rules.FeeRuleResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-rules")
public interface FeeRuleFeignClient {

    @PostMapping("/rules/fee/calculateOrderFee")
    Result<FeeRuleResponseVo> calculateOrderFee(@RequestBody FeeRuleRequestForm feeRuleRequestForm);
}