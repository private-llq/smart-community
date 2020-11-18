package com.jsy.community.config.web;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
@EnableKnife4j
@ConditionalOnProperty(value = "jsy.web.enable", havingValue = "true")
public class Swagger3Config {
	/**
	 * 业主端接口文档
	 */
	@Bean
	public Docket createProprietorApi() {
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
