package com.jsy.community;

//import com.jsy.community.task.mqtt.MqttPushClient;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableAsync
@DubboComponentScan
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@MapperScan("com.jsy.community.mapper")
@PropertySource(value = "classpath:common-service.properties")
// @EnableDistributedTransaction
public class ProprietorServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(ProprietorServiceApp.class, args);
	}
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
		return interceptor;
	}
	
}