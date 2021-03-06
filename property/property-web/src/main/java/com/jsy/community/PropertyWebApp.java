package com.jsy.community;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.PropertySource;

@EnableCaching
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@DubboComponentScan
@ComponentScans(value = {@ComponentScan("com.zhsj"), @ComponentScan("com.jsy.community")})
@PropertySource(value = "classpath:common-web.properties")
@EnableDiscoveryClient
public class PropertyWebApp {
	public static void main(String[] args) {
		SpringApplication.run(PropertyWebApp.class, args);
	}
}
