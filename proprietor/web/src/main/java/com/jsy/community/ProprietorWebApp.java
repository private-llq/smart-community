package com.jsy.community;


import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@DubboComponentScan
public class ProprietorWebApp {
	public static void main(String[] args) {
		SpringApplication.run(ProprietorWebApp.class, args);
	}
}
