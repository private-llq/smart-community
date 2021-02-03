package com.jsy.community;

import com.codingapi.txlcn.tm.config.EnableTransactionManagerServer;
import com.jsy.community.config.service.RedisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableTransactionManagerServer
@ComponentScan(value = "com.jsy.community", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {RedisConfig.class}))
public class TxManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TxManagerApplication.class, args);
	}

}
