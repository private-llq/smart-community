package com.jsy.community.config.web;

import com.jsy.community.annotation.web.ApiAdmin;
import com.jsy.community.annotation.web.ApiProperty;
import com.jsy.community.annotation.web.ApiProprietor;
import com.jsy.community.intercepter.AuthorizationInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * MVC配置
 */
@Configuration
@ConditionalOnProperty(value = "jsy.web.enable", havingValue = "true")
public class WebMvcConfig implements WebMvcConfigurer {
	@Resource
	private AuthorizationInterceptor authorizationInterceptor;
	
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer
			.addPathPrefix("/api/v1/proprietor", c -> c.isAnnotationPresent(ApiProprietor.class))
			.addPathPrefix("/api/v1/property", c -> c.isAnnotationPresent(ApiProperty.class))
			.addPathPrefix("/api/v1/admin", c -> c.isAnnotationPresent(ApiAdmin.class));
	}
	
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