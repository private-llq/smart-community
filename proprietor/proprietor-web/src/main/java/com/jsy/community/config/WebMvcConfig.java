package com.jsy.community.config;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.ApiOutController;
import com.jsy.community.intercepter.AuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * MVC配置
 */
@Configuration
@ConditionalOnProperty(value = "jsy.web.enable", havingValue = "true")
public class WebMvcConfig implements WebMvcConfigurer {
	
	/*@Value("${spring.application.name}")
	private String name;*/
	
	/*private String prefix = "/api/v1/";
	private String prefix2 = "";*/
	
	/*@PostConstruct
	public void init() {
		prefix += name.split("-")[0];
		prefix2 += prefix + "/out";
	}*/
	
	/*@Resource
	private AuthorizationInterceptor authorizationInterceptor;*/
	
	/*@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer
			// .addPathPrefix(prefix, c -> c.isAnnotationPresent(ApiJSYController.class))ApiJSYController
			.addPathPrefix(prefix2, c -> c.isAnnotationPresent(ApiOutController.class));
	}*/
	
	/*@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authorizationInterceptor).addPathPatterns("/**");
	}*/
	
	/*@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowCredentials(true)
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
			.maxAge(3600);
	}*/
}