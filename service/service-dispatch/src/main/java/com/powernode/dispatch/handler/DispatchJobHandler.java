package com.powernode.dispatch.handler;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * xxl-job handler
 */
@Slf4j
@Component
public class DispatchJobHandler {

    @XxlJob("myJobHandler")
    public void myJobHandler() {
        log.info("myJobHandler...");
    }
}
