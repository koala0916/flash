package com.powernode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author:pat
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ServiceDispatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceDispatchApplication.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

