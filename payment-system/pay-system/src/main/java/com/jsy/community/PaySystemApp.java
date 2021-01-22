package com.jsy.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-01-22 14:00
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class PaySystemApp {
    public static void main(String[] args) {
        SpringApplication.run(PaySystemApp.class);
    }
}
