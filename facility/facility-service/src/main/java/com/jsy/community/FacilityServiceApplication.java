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
@EnableDiscoveryClient
@MapperScan("com.jsy.community.mapper")
@PropertySource(value = "classpath:common-service.properties")
@EnableDistributedTransaction
public class FacilityServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(FacilityServiceApplication.class, args);
	}
	
}
