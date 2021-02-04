package com.jsy.community.config.service;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * Redis配置
 * @author ling
 */
@Configuration
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

	//@Value("${jsy.redis.annotation.cacheTimeout}")
	private Integer cacheTimeout = 1800;

	@Bean
	public RedissonClient redissonClient(){
		Config config = new Config();
		SingleServerConfig singleServerConfig = config.useSingleServer();
		singleServerConfig.setAddress("redis://"+ redisHost +":" + redisPort);
		singleServerConfig.setDatabase(redisDatabase);
		singleServerConfig.setPassword(redisPassword);
		singleServerConfig.setConnectTimeout(3000);
		singleServerConfig.setIdleConnectionTimeout(3000);
		singleServerConfig.setRetryInterval(1000);
		//重连次数
		singleServerConfig.setRetryAttempts(5);
		return Redisson.create(config);
	}

	@Bean()
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(factory);
		return redisTemplate;
	}


	/**
	 * 此配置用于@Cacheable注解
	 * RedisSerializer
	 * 自定义序列化类参照：https://docs.spring.io/spring-data/redis/docs/current/api/
	 * GenericJackson2JsonRedisSerializer - 基于通用Jackson的2 RedisSerializer，objects使用动态类型映射到JSON。
	 * GenericToStringSerializer          - 通用字符串转为byte []（并返回）序列化器。依靠SpringConversionService将对象转换为String，反之亦然。使用指定的字符集（默认为UTF-8）将字符串转换为字节，反之亦然。注意：如果将类定义为Spring bean，则转换服务初始化会自动发生。注意：不会以任何特殊方式处理null，将所有内容委派给容器。
	 * Jackson2JsonRedisSerializer        - 该转换器可用于绑定到类型化的bean或未类型化的HashMap实例。 注意：空对象被序列化为空数组，反之亦然。
	 * JdkSerializationRedisSerializer    - Java序列化Redis序列化器。代表默认值（基于Java）serializer和 DefaultDeserializer。这serializer可以用customClassLoader或own构造 converters。
	 * OxmSerializer                      - Spring的O / X映射之上的Serializer适配器。将序列化/反序列化委托给OXMMarshaller 和Unmarshaller。注意：空对象被序列化为空数组，反之亦然。
	 * StringRedisSerializer              - 简单String到byte []（和返回）串行器。Strings 使用指定的字符集（默认为UTF-8）转换为字节，反之亦然。与Redis的交互主要通过字符串进行时很有用。由于空字符串是有效的键/值，因此不执行任何null转换。
	 */
	@Bean
	public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
		//设置CacheManager的值序列化方式为json序列化
		RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
		//设置值的序列化方式
		RedisCacheConfiguration defaultCacheConfig= RedisCacheConfiguration.defaultCacheConfig()
				.serializeValuesWith(RedisSerializationContext
						.SerializationPair
						.fromSerializer(jsonSerializer)
				).entryTtl(Duration.ofSeconds(this.cacheTimeout));
		//设置key的序列化方式
		RedisSerializationContext.SerializationPair<String> stringSerializationPair = RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());
		defaultCacheConfig.serializeKeysWith(stringSerializationPair);
		return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(defaultCacheConfig).build();
	}

}