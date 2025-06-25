package com.powernode.rules.controller;

import com.powernode.common.result.Result;
import com.powernode.model.form.rules.FeeRuleRequestForm;
import com.powernode.model.vo.rules.FeeRuleResponseVo;
import com.powernode.rules.service.FeeRuleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rules/fee")
@SuppressWarnings({"unchecked", "rawtypes"})
public class FeeRuleController {

    @Resource
    private FeeRuleService feeRuleService;


    @Operation(summary = "预估计算配送费用")
    @PostMapping("/calculateOrderFee")
    public Result<FeeRuleResponseVo> calculateOrderFee(@RequestBody FeeRuleRequestForm feeRuleRequestForm) {
        FeeRuleResponseVo feeRuleResponseVo = feeRuleService.calculateOrderFee(feeRuleRequestForm);
        return Result.ok(feeRuleResponseVo);
    }
}

