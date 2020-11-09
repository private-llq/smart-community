package com.jsy.community;


import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@DubboComponentScan
@EnableOpenApi
public class ProprietorWebApp {
	public static void main(String[] args) {
		SpringApplication.run(ProprietorWebApp.class, args);
	}
}
