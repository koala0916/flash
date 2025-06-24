package com.powernode.driver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "tencent.cloud")
@Data
@Component
public class TencentProperties {
    private String secretId;
    private String secretKey;
    private String region;
    private String bucketPrivate;
    private String persionGroupId;
}