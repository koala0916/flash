package com.powernode.common.config.redisson;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * redisson的配置类
 */
@Data
@Configuration
@ConfigurationProperties("spring.data.redis")
public class RedissonConfig {

    private String host;
    private String port;
    private String password;
    private int timeout = 3000;

    private static String ADDRESS_PREFIX = "redis://";


    @Bean
    public RedissonClient redissonSingle(){
        Config config = new Config();

        if (!StringUtils.hasText(host)) {
            throw new RuntimeException("redis host can not be null");
        }

        SingleServerConfig singleServerConfig = config.useSingleServer();
        //redis://192.168.75.128:6379  redisson与redis的通信地址
        singleServerConfig.setAddress(ADDRESS_PREFIX + host + ":" + port);
        singleServerConfig.setTimeout(timeout);
        singleServerConfig.setPassword(password);


        return Redisson.create(config);
    }


}
