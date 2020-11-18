package com.jsy.community.config.web;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@ConditionalOnProperty(value = "jsy.web.enable", havingValue = "true")
public class LocalDateTimeSerializerConfig {
	
	@Value("${spring.jackson.date-format}")
	private String pattern;
	
	@Bean
	public LocalDateTimeSerializer localDateTimeDeserializer() {
		return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
	}
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		return builder -> builder.serializerByType(LocalDateTime.class, localDateTimeDeserializer());
	}
}