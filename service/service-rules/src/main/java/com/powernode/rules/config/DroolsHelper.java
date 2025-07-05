package com.powernode.rules.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

public class DroolsHelper {

    /**
     * 读取规则文件
     * @param drlStr
     * @return
     */
    public static KieSession loadForRule(String drlStr) {
        KieServices kieServices = KieServices.Factory.get();

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        kieFileSystem.write(ResourceFactory.newClassPathResource(drlStr));

        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);

        kb.buildAll();

        KieModule kieModule = kb.getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());


        return kieContainer.newKieSession();
    }
}
