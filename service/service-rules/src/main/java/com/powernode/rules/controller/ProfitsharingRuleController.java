package com.powernode.rules.controller;

import com.powernode.common.result.Result;
import com.powernode.model.form.rules.ProfitsharingRuleRequestForm;
import com.powernode.model.vo.rules.ProfitsharingRuleResponseVo;
import com.powernode.rules.service.ProfitsharingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rules/profitsharing")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProfitsharingRuleController {

    @Resource
    private ProfitsharingRuleService profileService;

    @Operation(summary = "计算分账金额")
    @PostMapping("/calculateOrderProfitsharingFee")
    public Result<ProfitsharingRuleResponseVo> calculateOrderProfitsharingFee(@RequestBody ProfitsharingRuleRequestForm profitsharingRuleRequestForm) {
        return Result.ok(profileService.calculateProfitsharingFee(profitsharingRuleRequestForm));
    }

}

