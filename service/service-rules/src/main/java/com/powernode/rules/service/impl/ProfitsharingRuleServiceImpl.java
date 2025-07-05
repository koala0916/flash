package com.powernode.rules.service.impl;


import com.powernode.model.form.rules.ProfitsharingRuleRequest;
import com.powernode.model.form.rules.ProfitsharingRuleRequestForm;
import com.powernode.model.vo.rules.ProfitsharingRuleResponse;
import com.powernode.model.vo.rules.ProfitsharingRuleResponseVo;
import com.powernode.rules.config.DroolsHelper;
import com.powernode.rules.mapper.ProfitsharingRuleMapper;
import com.powernode.rules.service.ProfitsharingRuleService;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProfitsharingRuleServiceImpl implements ProfitsharingRuleService {

    @Autowired
    private ProfitsharingRuleMapper rewardRuleMapper;


    /**
     * 计算分账信息
     */
    @Override
    public ProfitsharingRuleResponseVo calculateProfitsharingFee(ProfitsharingRuleRequestForm profitsharingRuleRequestForm) {
        //创建入参
        ProfitsharingRuleRequest profitsharingRuleRequest = new ProfitsharingRuleRequest();
        profitsharingRuleRequest.setOrderAmount(profitsharingRuleRequestForm.getOrderAmount());
        profitsharingRuleRequest.setOrderNum(profitsharingRuleRequestForm.getOrderNum());

        KieSession kieSession = DroolsHelper.loadForRule("rules/ProfitsharingRule.drl");

        //创建出参
        ProfitsharingRuleResponse profitsharingRuleResponse = new ProfitsharingRuleResponse();
        kieSession.setGlobal("profitsharingRuleResponse", profitsharingRuleResponse);

        //入参
        kieSession.insert(profitsharingRuleRequest);
        kieSession.fireAllRules();
        kieSession.dispose();


        ProfitsharingRuleResponseVo profitsharingRuleResponseVo = new ProfitsharingRuleResponseVo();

        BeanUtils.copyProperties(profitsharingRuleResponse, profitsharingRuleResponseVo);

        return profitsharingRuleResponseVo;
    }
}
