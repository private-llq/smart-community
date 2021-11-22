package com.jsy.community.config.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "jsy.service.enable", havingValue = "true")
public class RedisLockConfiguration {
//	@Bean
//	public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
//		return new RedisLockRegistry(redisConnectionFactory, "spring-cloud");
//	}
}