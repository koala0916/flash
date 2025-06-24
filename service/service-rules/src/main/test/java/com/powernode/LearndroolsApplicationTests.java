package com.powernode;

import com.alibaba.fastjson.JSON;
import com.powernode.model.form.rules.FeeRuleRequest;
import com.powernode.model.vo.rules.FeeRuleResponse;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class LearndroolsApplicationTests {

    @Resource
    private KieContainer kieContainer;

    @Test
    void contextLoads() {


        FeeRuleRequest feeRuleRequest = new FeeRuleRequest();

        feeRuleRequest.setDistance(new BigDecimal(10));
        feeRuleRequest.setStartTime("10:00:00");
        feeRuleRequest.setWaitMinute(12);

        //开启会话
        KieSession kieSession = kieContainer.newKieSession();

        FeeRuleResponse feeRuleResponse = new FeeRuleResponse();
        //设置drools全局变量
        kieSession.setGlobal("feeRuleResponse", feeRuleResponse);
        //传入入参
        kieSession.insert(feeRuleRequest);
        //触发规则
        kieSession.fireAllRules();
        kieSession.dispose();

        System.out.println(JSON.toJSONString(feeRuleResponse));
    }

}
