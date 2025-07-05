package com.powernode.rules.service.impl;


import com.powernode.model.form.rules.RewardRuleRequest;
import com.powernode.model.form.rules.RewardRuleRequestForm;
import com.powernode.model.vo.rules.RewardRuleResponse;
import com.powernode.model.vo.rules.RewardRuleResponseVo;
import com.powernode.rules.config.DroolsHelper;
import com.powernode.rules.mapper.RewardRuleMapper;
import com.powernode.rules.service.RewardRuleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class RewardRuleServiceImpl implements RewardRuleService {

    @Resource
    private RewardRuleMapper rewardRuleMapper;

    /**
     * 计算奖励规则
     * @param rewardRuleRequestForm
     * @return
     */
    @Override
    public RewardRuleResponseVo calculateOrderRewardFee(RewardRuleRequestForm rewardRuleRequestForm) {
        RewardRuleRequest rewardRuleRequest = new RewardRuleRequest();
        rewardRuleRequest.setOrderNum(rewardRuleRequestForm.getOrderNum());//订单个数

        //加载规则
        KieSession kieSession = DroolsHelper.loadForRule("rules/RewardRule.drl");

        //出参
        RewardRuleResponse rewardRuleResponse = new RewardRuleResponse();
        kieSession.setGlobal("rewardRuleResponse", rewardRuleResponse);
        //入参
        kieSession.insert(rewardRuleRequest);

        //触发规则
        kieSession.fireAllRules();
        //终止
        kieSession.dispose();

        RewardRuleResponseVo rewardRuleResponseVo = new RewardRuleResponseVo();
        rewardRuleResponseVo.setRewardRuleId(1L);
        rewardRuleResponseVo.setRewardAmount(rewardRuleResponse.getRewardAmount());

        return rewardRuleResponseVo;
    }
}
