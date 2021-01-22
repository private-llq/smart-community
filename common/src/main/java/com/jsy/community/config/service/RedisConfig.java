package com.jsy.community.config.service;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;

/**
 * Redis配置
 */
@Configuration
@ConditionalOnProperty(value = "jsy.service.enable", havingValue = "true")
public class RedisConfig {

	@Resource
	private RedisConnectionFactory factory;


	@Value("${spring.redis.port}")
	private Integer redisPort;

	@Value("${spring.redis.host}")
	private String redisHost;

	@Value("${spring.redis.password}")
	private String redisPassword;

	@Value("${spring.redis.database}")
	private Integer redisDatabase;

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(factory);
		return redisTemplate;
	}

	@Bean
	public RedissonClient redissonClient(){
		Config config = new Config();
		SingleServerConfig singleServerConfig = config.useSingleServer();
		singleServerConfig.setAddress("redis://"+ redisHost +":" + redisPort);
		singleServerConfig.setDatabase(redisDatabase);
		singleServerConfig.setPassword(redisPassword);
		return Redisson.create(config);
	}
	
}