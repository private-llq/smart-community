package com.jsy.community.config.service;

import me.zhyd.oauth.cache.AuthStateCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redis作为JustAuth的State的缓存
 *
 * @author ling
 * @date 2020-11-16 17:24
 */
@Configuration
@RefreshScope
@ConditionalOnProperty(value = "jsy.module.name", havingValue = "service")
public class RedisStateCache implements AuthStateCache {
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	/**
	 * 缓存超时时间，默认3分钟
	 */
	@Value("${justauth.cache.timeout:180}")
	private Integer timeout;
	
	@Value("${justauth.cache.prefix:JUSTAUTH::STATE::}")
	private String prefix;
	
	/**
	 * 存入缓存
	 *
	 * @param key   缓存key
	 * @param value 缓存内容
	 */
	@Override
	public void cache(String key, String value) {
		this.cache(key, value, timeout);
	}
	
	/**
	 * 存入缓存
	 *
	 * @param key     缓存key
	 * @param value   缓存内容
	 * @param timeout 指定缓存过期时间（毫秒）
	 */
	@Override
	public void cache(String key, String value, long timeout) {
		redisTemplate.opsForValue().set(prefix + key, value, timeout, TimeUnit.SECONDS);
	}
	
	/**
	 * 获取缓存内容
	 *
	 * @param key 缓存key
	 * @return 缓存内容
	 */
	@Override
	public String get(String key) {
		return redisTemplate.opsForValue().get(prefix + key);
	}
	
	/**
	 * 是否存在key，如果对应key的value值已过期，也返回false
	 *
	 * @param key 缓存key
	 * @return true：存在key，并且value没过期；false：key不存在或者已过期
	 */
	@Override
	public boolean containsKey(String key) {
		Long expire = redisTemplate.getExpire(prefix + key, TimeUnit.MILLISECONDS);
		if (expire == null) {
			expire = 0L;
		}
		return expire > 0;
	}
}