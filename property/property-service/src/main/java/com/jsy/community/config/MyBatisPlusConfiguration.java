package com.jsy.community.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyBatisPlusConfiguration {
	@Value("${jsy.test}")
	private Integer value;
	
	@PostConstruct
	public void init() {
		System.out.println(value);
	}
	
	/**
	 * 分页插件，待定
	 */
	@Bean
	public MybatisPlusInterceptor paginationInterceptor() {
		return new MybatisPlusInterceptor();
	}
}