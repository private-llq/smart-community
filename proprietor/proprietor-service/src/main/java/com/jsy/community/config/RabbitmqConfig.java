package com.jsy.community.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
* @Description: Rabbitmq配置类
 * @Author: chq459799974
 * @Date: 2021/6/26
**/
@Configuration
@Slf4j
public class RabbitmqConfig {

	@Autowired
	private CachingConnectionFactory connectionFactory;

	@Bean
	public RabbitAdmin rabbitAdmin() {
		return new RabbitAdmin(connectionFactory);
	}

}
