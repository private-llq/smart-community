package com.jsy.community.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfiguration {
	
	/**
	 * 分页插件，待定
	 */
	@Bean
	public MybatisPlusInterceptor paginationInterceptor() {
		return new MybatisPlusInterceptor();
	}
}