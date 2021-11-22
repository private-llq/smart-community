package com.jsy.community;


import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@EnableCaching
@SpringBootApplication
@DubboComponentScan
@ComponentScan(value = "com.zhsj")
@EnableDiscoveryClient
@PropertySource(value = "classpath:common-web.properties")
public class ProprietorWebApp {
	public static void main(String[] args) {
		SpringApplication.run(ProprietorWebApp.class, args);
	}


}
