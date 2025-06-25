package com.powernode.rules.service.impl;


import com.powernode.model.form.rules.FeeRuleRequest;
import com.powernode.model.form.rules.FeeRuleRequestForm;
import com.powernode.model.vo.rules.FeeRuleResponse;
import com.powernode.model.vo.rules.FeeRuleResponseVo;
import com.powernode.rules.service.FeeRuleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class FeeRuleServiceImpl implements FeeRuleService {

    @Resource
    private KieContainer kieContainer;


    @Override
    public FeeRuleResponseVo calculateOrderFee(FeeRuleRequestForm feeRuleRequestForm) {

        //创建request对象 入参
        FeeRuleRequest feeRuleRequest = new FeeRuleRequest();

        feeRuleRequest.setDistance(feeRuleRequestForm.getDistance());
        feeRuleRequest.setStartTime(new DateTime(feeRuleRequestForm.getStartTime()).toString("HH:mm:ss"));
        feeRuleRequest.setWaitMinute(feeRuleRequestForm.getWaitMinute());

        //开启会话
        KieSession kieSession = kieContainer.newKieSession();
        //设置全局变量
        FeeRuleResponse feeRuleResponse = new FeeRuleResponse();
        kieSession.setGlobal("feeRuleResponse",feeRuleResponse);

        //设置入参
        kieSession.insert(feeRuleRequest);
        //触发规则
        kieSession.fireAllRules();
        kieSession.dispose();//终止会话

        //封装返回结果
        FeeRuleResponseVo feeRuleResponseVo = new FeeRuleResponseVo();

        BeanUtils.copyProperties(feeRuleResponse, feeRuleResponseVo);

        return feeRuleResponseVo;
    }
}
