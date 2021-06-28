package com.jsy.community.config;

import com.jsy.community.listener.handler.MessageConsumerHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


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

	@Autowired
	private MessageConsumerHandler handler;
	

	@Bean
	public RabbitAdmin rabbitAdmin() {
		return new RabbitAdmin(connectionFactory);
	}

	@Bean
	@Order(value = 2)
	public SimpleMessageListenerContainer mqMessageContainer() throws AmqpException {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
//		List<String> list = queueService.getMQJSONArray();
//		container.setQueueNames(list.toArray(new String[list.size()]));
//		container.setExposeListenerChannel(true);
//		container.setPrefetchCount(1);//设置每个消费者获取的最大的消息数量
//		container.setConcurrentConsumers(100);//消费者个数
//		container.setAcknowledgeMode(AcknowledgeMode.AUTO);//设置确认模式为手工确认
		container.setMessageListener(handler);//监听处理类
		return container;
	}

}
