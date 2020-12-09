package com.jsy.community;


import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication
@DubboComponentScan
@PropertySource(value = "classpath:common-web.properties")
public class ProprietorWebApp {
	public static void main(String[] args) {
		SpringApplication.run(ProprietorWebApp.class, args);
	}
	@Bean
	public RequestContextListener requestContextListener(){
		return new RequestContextListener();
	}
}
