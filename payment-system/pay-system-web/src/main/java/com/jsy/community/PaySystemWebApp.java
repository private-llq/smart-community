package com.jsy.community;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-01-22 14:00
 **/
@SpringBootApplication
@DubboComponentScan
@PropertySource(value = "classpath:common-web.properties")
public class PaySystemWebApp {
    public static void main(String[] args) {
        SpringApplication.run(PaySystemWebApp.class);
    }
}
