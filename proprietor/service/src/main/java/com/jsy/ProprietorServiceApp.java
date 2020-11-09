package com.jsy;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@DubboComponentScan
@SpringBootApplication
public class ProprietorServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(ProprietorServiceApp.class, args);
	}
}