package com.bjpowernode.learndrools.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DroolsConfig {

    //通过KieServices 来操作drools
    private static final KieServices KES_SERVICES = KieServices.Factory.get();

    //规则文件的路径
    private static final String RULES_CUSTOMER_RULES_DRL = "rules/user.drl";


    /**
     * kie 全称 knowledge is everything
     * @return
     */
    @Bean
    public KieContainer kieContainer() {
        //创建内存文件系统
        KieFileSystem kieFileSystem = KES_SERVICES.newKieFileSystem();

        //将我们的drool配置文件写入到内存文件中
        kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_CUSTOMER_RULES_DRL));

        //创建一个构建器
        KieBuilder kieBuilder = KES_SERVICES.newKieBuilder(kieFileSystem);

        kieBuilder.buildAll();

        KieModule kieModule = kieBuilder.getKieModule();

        KieContainer kieContainer = KES_SERVICES.newKieContainer(kieModule.getReleaseId());
        return kieContainer;
    }
}
