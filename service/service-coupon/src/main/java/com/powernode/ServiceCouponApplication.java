package com.powernode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author:pat
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceCouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCouponApplication.class, args);
    }
}
