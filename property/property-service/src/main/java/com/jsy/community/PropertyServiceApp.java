package com.jsy.community;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;


@DubboComponentScan
@SpringBootApplication
@EnableScheduling
@MapperScan("com.jsy.community.mapper")
@PropertySource(value = "classpath:common-service.properties")
@EnableDistributedTransaction
@EnableDiscoveryClient
public class PropertyServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(PropertyServiceApp.class, args);
	}
}