package com.jsy.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-01-25 16:48
 **/
@SpringBootApplication
@PropertySource(value = "classpath:common-service.properties")
public class PaySystemServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(PaySystemServiceApp.class);
    }
}
