package com.jsy.community;


import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@DubboComponentScan
@PropertySource(value = "classpath:common-web.properties")
public class LeaseWebApp {
	public static void main(String[] args) {
		SpringApplication.run(LeaseWebApp.class, args);
	}
}
