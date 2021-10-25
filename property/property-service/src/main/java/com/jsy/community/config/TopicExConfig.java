package com.jsy.community.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description topic交换机配置
 * @since 2021-02-02 15:08
 **/
@Configuration
public class TopicExConfig {

//	@Value("${topic-environment}")
//	public static final String ENVIRONMENT = "20";
	
	//小区相关-声明交换机
	@Bean
	TopicExchange topicExOfToCommunity() {
		return new TopicExchange(PropertyTopicNameEntity.exTopicToCommunity);
	}
	//小区相关-声明队列
	@Bean
	public Queue queueOf2Community() {
		return new Queue(PropertyTopicNameEntity.queueToCommunity,true);
	}
	//小区相关-队列绑定交换机
	@Bean
	Binding bindingOfTopicEx2CommunityAndQueue2Community() {
		return BindingBuilder.bind(queueOf2Community()).to(topicExOfToCommunity()).with(PropertyTopicNameEntity.queueToCommunity);
	}



	//声明队列
	@Bean
	public Queue queueOfXUFaceServer() {
		return new Queue(PropertyTopicNameEntity.topicFaceXuServer,true);
	}
	@Bean
	public Queue queueOfXUFaceClient() {
		return new Queue(PropertyTopicNameEntity.topicFaceXuClient,true);
	}
	//声明交换机
	@Bean
	TopicExchange topicExOfXUFace() {
		return new TopicExchange(PropertyTopicNameEntity.exFaceXu);
	}
	//队列绑定交换机
	@Bean
	Binding bindingExchangeMessage1() {
		return BindingBuilder.bind(queueOfXUFaceServer()).to(topicExOfXUFace()).with(PropertyTopicNameEntity.topicFaceXuServer);
	}
	//队列绑定交换机
	@Bean
	Binding bindingExchangeMessage2() {
		return BindingBuilder.bind(queueOfXUFaceClient()).to(topicExOfXUFace()).with(PropertyTopicNameEntity.topicFaceXuClient);
	}



	//声明延时交换机
	@Bean("DelayExOfXUFace")
	CustomExchange delayExchangeExOfXUFace() {
		Map<String, Object> args = new HashMap<>(1);
		args.put("x-delayed-type", "direct");
		return new CustomExchange(PropertyTopicNameEntity.delayExFaceXu, "x-delayed-message", true, false, args);
	}

	//声明延时队列
	@Bean("delay.face.xu.server")
	public Queue delayQueueOfXUFaceServer() {
		return new Queue(PropertyTopicNameEntity.delayFaceXuServer, true);
	}

	//延时队列绑定延时交换机
	@Bean
	Binding bindingExchangeMessage3(@Qualifier("delay.face.xu.server") Queue queue,
									@Qualifier("DelayExOfXUFace") Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(PropertyTopicNameEntity.delayFaceXuServer).noargs();
	}

}
