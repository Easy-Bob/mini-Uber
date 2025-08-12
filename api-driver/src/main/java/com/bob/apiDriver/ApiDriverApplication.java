package com.bob.apiDriver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ApiDriverApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiDriverApplication.class, args);
    }
}
