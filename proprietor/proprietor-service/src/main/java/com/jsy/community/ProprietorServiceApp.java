package com.jsy.community;

//import com.jsy.community.task.mqtt.MqttPushClient;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableAsync
@DubboComponentScan
@SpringBootApplication
@EnableScheduling
@MapperScan("com.jsy.community.mapper")
@PropertySource(value = "classpath:common-service.properties")
public class ProprietorServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(ProprietorServiceApp.class, args);
	}
	
}