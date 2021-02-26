package com.jsy.community.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * @author YuLF
 * @since 2021-02-26 14:36
 */
@Configuration
public class AdminConfiguration {

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Value("${spring.redis.sms-databases}")
    private Integer smsDatabases;


    @Bean("adminRedisTemplate")
    public RedisTemplate<String, Object> adminRedisTemplate(RedisConnectionFactory sevenRedisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(sevenRedisConnectionFactory);
        return redisTemplate;
    }

    /**
     * redis 7 号数据库连接
     */
    @Bean("sevenRedisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder poolingClientConfigurationBuilder =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder)JedisClientConfiguration.builder();
        JedisClientConfiguration jedisClientConfiguration = poolingClientConfigurationBuilder.build();
        redisStandaloneConfiguration.setDatabase(smsDatabases);
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setPassword(redisPassword);
        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
    }

}
