package com.jsy.community;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@DubboComponentScan
@SpringBootApplication
@MapperScan("com.jsy.community.mapper")
public class ProprietorServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(ProprietorServiceApp.class, args);
	}
}