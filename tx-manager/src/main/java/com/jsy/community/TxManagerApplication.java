package com.jsy.community;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@DubboComponentScan
@MapperScan("com.jsy.community.mapper")
public class TxManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TxManagerApplication.class, args);
	}

}
