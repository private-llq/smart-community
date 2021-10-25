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

	//小区相关-声明交换机
	@Bean
	TopicExchange topicExOfVisitorToCommunity() {
		return new TopicExchange(ProprietorTopicNameEntity.exTopicVisitorToCommunity);
	}
	//小区相关-声明队列
	@Bean
	public Queue queueOfVisitor2Community() {
		return new Queue(ProprietorTopicNameEntity.queueVisitorToCommunity,true);
	}
	//小区相关-队列绑定交换机
	@Bean
	Binding bindingOfVisitorTopicExAndQueue() {
		return BindingBuilder.bind(queueOfVisitor2Community()).to(topicExOfVisitorToCommunity()).with(ProprietorTopicNameEntity.queueVisitorToCommunity);
	}





	//声明队列
	@Bean
	public Queue queueOfXUFaceServer() {
		return new Queue(ProprietorTopicNameEntity.topicFaceXuServer,true);
	}
	@Bean
	public Queue queueOfXUFaceClient() {
		return new Queue(ProprietorTopicNameEntity.topicFaceXuClient,true);
	}
	//声明交换机
	@Bean
	TopicExchange topicExOfXUFace() {
		return new TopicExchange(ProprietorTopicNameEntity.exFaceXu);
	}
	//队列绑定交换机
	@Bean
	Binding bindingExchangeMessage1() {
		return BindingBuilder.bind(queueOfXUFaceServer()).to(topicExOfXUFace()).with(ProprietorTopicNameEntity.topicFaceXuServer);
	}
	//队列绑定交换机
	@Bean
	Binding bindingExchangeMessage2() {
		return BindingBuilder.bind(queueOfXUFaceClient()).to(topicExOfXUFace()).with(ProprietorTopicNameEntity.topicFaceXuClient);
	}

}
