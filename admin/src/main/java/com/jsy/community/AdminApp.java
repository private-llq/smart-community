package com.jsy.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication(scanBasePackages = {"com.jsy.common"})
@PropertySources({
	@PropertySource(value = "classpath:common-web.properties"),
	@PropertySource(value = "classpath:common-service.properties")
})
public class AdminApp {
	public static void main(String[] args) {
		SpringApplication.run(AdminApp.class, args);
	}
}
