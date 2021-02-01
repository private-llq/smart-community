package com.jsy.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-17 13:31
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class RabbitApp {
    public static void main(String[] args) {
        SpringApplication.run(RabbitApp.class);
    }
}
