package com.jsy.community;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources({
	@PropertySource(value = "classpath:common-web.properties"),
	@PropertySource(value = "classpath:common-service.properties")
})
@DubboComponentScan
@EnableDiscoveryClient
@ComponentScans(value = {@ComponentScan("com.zhsj"), @ComponentScan("com.jsy.community")})
@MapperScan("com.jsy.community.mapper")
@EnableDistributedTransaction
public class AdminApp {
	public static void main(String[] args) {
		SpringApplication.run(AdminApp.class, args);
	}
}
