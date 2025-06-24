package com.bjpowernode.learndrools;

import com.bjpowernode.learndrools.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LearndroolsApplicationTests {

    @Resource
    private KieContainer kieContainer;

    @Test
    void contextLoads() {
        KieSession kieSession = kieContainer.newKieSession();

        User user = new User();
        user.setAge(10);

        //将对象传入drools配置文件
        kieSession.insert(user);

        //激活规则
        kieSession.fireAllRules();
        kieSession.dispose();
        System.out.println(user);

    }

}
