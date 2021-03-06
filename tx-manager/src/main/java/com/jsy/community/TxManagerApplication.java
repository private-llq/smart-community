package com.jsy.community;

import com.codingapi.txlcn.tm.config.EnableTransactionManagerServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
//@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@EnableTransactionManagerServer
@EnableDiscoveryClient
//@ComponentScan(value = "com.jsy.community", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {RedisConfig.class}))
public class TxManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TxManagerApplication.class, args);

//		SpringApplication app = new SpringApplication(TxManagerApplication.class);
//		app.addListeners(new ApplicationPidFileWriter("app.pid"));
//		app.run(args);
	}

}
