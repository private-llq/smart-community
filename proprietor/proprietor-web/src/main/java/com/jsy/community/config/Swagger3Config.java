package com.jsy.community.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.jsy.community.annotation.web.ApiProperty;
import com.jsy.community.annotation.web.ApiProprietor;
import io.swagger.annotations.ApiOperation;
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
public class Swagger3Config {
	/**
	 * 业主端接口文档
	 */
	@Bean
	public Docket createProprietorApi() {
		return new Docket(DocumentationType.OAS_30)
			.apiInfo(apiInfo())
			.groupName("业主端")
			.select()
			.apis(RequestHandlerSelectors.withClassAnnotation(ApiProprietor.class))
			.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
			.paths(PathSelectors.any())
			.build();
	}
	
	/**
	 * 物业端接口文档
	 */
	@Bean
	public Docket createPropertyApi() {
		return new Docket(DocumentationType.OAS_30)
			.apiInfo(apiInfo())
			.groupName("物业端")
			.select()
			.apis(RequestHandlerSelectors.withClassAnnotation(ApiProperty.class))
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
