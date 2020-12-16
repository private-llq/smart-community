package com.jsy.lease;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@DubboComponentScan
@SpringBootApplication
@EnableScheduling
@MapperScan("com.jsy.lease.mapper")
@PropertySource(value = "classpath:common-service.properties")
public class LeaseServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(LeaseServiceApp.class, args);
	}
}