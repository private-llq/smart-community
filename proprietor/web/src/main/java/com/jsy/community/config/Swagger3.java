package com.jsy.community.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class Swagger3 {
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.OAS_30)
			.apiInfo(apiInfo())
			.select()
			.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
			.paths(PathSelectors.any())
			.build();
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("智慧社区接口文档")
			.contact(new Contact("Ling", null, "503580622@qq.com"))
			.version("1.0")
			.build();
	}
}
