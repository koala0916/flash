package com.powernode.rules.controller;

import com.powernode.common.result.Result;
import com.powernode.model.form.rules.RewardRuleRequestForm;
import com.powernode.model.vo.rules.RewardRuleResponseVo;
import com.powernode.rules.service.RewardRuleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rules/reward")
@SuppressWarnings({"unchecked", "rawtypes"})
public class RewardRuleController {

    @Resource
    private RewardRuleService rewardRuleService;

    @Operation(summary = "计算订单奖励金额")
    @PostMapping("/calculateOrderRewardFee")
    public Result<RewardRuleResponseVo> calculateOrderRewardFee(@RequestBody RewardRuleRequestForm rewardRuleRequestForm) {
        return Result.ok(rewardRuleService.calculateOrderRewardFee(rewardRuleRequestForm));
    }
}

