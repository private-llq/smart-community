package com.jsy.community;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;

@EnableCaching
@SpringBootApplication
@DubboComponentScan
@PropertySource(value = "classpath:common-web.properties")
public class PropertyWebApp {
	public static void main(String[] args) {
		SpringApplication.run(PropertyWebApp.class, args);
	}
}
