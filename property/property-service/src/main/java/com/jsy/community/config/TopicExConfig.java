package com.jsy.community.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chq459799974
 * @description topic交换机配置
 * @since 2021-02-02 15:08
 **/
@Configuration
public class TopicExConfig {


	//声明交换机
	@Bean
	TopicExchange topicExOfProperty() {
		return new TopicExchange(RabbitMQConfig.EX_PROPERTY);
	}

	//声明队列
	@Bean
	public Queue queueOfPropertyVisitor() {
		return new Queue(RabbitMQConfig.TOPIC_PROPERTY_VISITOR_RECORD,true);
	}

	//队列绑定交换机
	@Bean
	Binding bindingOfVisitorRecord() {
		return BindingBuilder.bind(queueOfPropertyVisitor()).to(topicExOfProperty()).with(RabbitMQConfig.TOPIC_PROPERTY_VISITOR_RECORD);
	}

}
