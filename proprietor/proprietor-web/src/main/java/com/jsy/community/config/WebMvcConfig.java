package com.jsy.community.config;

import com.jsy.community.intercepter.AuthorizationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Resource
	private AuthorizationInterceptor authorizationInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authorizationInterceptor).addPathPatterns("/**");
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowCredentials(true)
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
			.maxAge(3600);
	}
}