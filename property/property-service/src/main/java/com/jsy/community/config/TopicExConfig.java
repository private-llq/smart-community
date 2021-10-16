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
	
	//小区相关-topic交换机名称
	public final static String EX_TOPIC_TO_COMMUNITY = "cloud2CommunityTopicExchange";
	
	//绑定topic名称
	public final static String QUEUE_TO_COMMUNITY = "queue.2community"; //小区监听云端队列(根据社区id加后缀动态创建) 如小区id是1 创建出来就是queue.community.1 小区id是2 创建出来就是queue.community.2
	
	//监听topic名称
	public final static String QUEUE_FROM_COMMUNITY = "queue.2cloud"; //云端监听小区队列，根据参数communityId判断是哪个小区(摄像头相关)
	// TODO 本地测试为了不被线上环境消费加了'.19'
	public final static String QUEUE_VISITOR_HIS_FROM_COMMUNITY = "queue.visitor.his.2cloud.20"; //云端监听小区队列，根据参数communityId判断是哪个小区(访客记录相关)
	
	//小区相关-声明交换机
	@Bean
	TopicExchange topicExOfToCommunity() {
		return new TopicExchange(EX_TOPIC_TO_COMMUNITY);
	}
	//小区相关-声明队列
	@Bean
	public Queue queueOf2Community() {
		return new Queue(QUEUE_TO_COMMUNITY,true);
	}
	//小区相关-队列绑定交换机
	@Bean
	Binding bindingOfTopicEx2CommunityAndQueue2Community() {
		return BindingBuilder.bind(queueOf2Community()).to(topicExOfToCommunity()).with(QUEUE_TO_COMMUNITY);
	}


	//交换机名称
	public final static String EX_FACE_XU = "topicExOfXUFace"; //炫优人脸识别一体机 - 交换机

	//绑定topic名称
	public final static String TOPIC_FACE_XU_SERVER = "topic.face.xu.server"; //炫优人脸识别一体机 - topic - server
	//监听topic名称
	public final static String TOPIC_FACE_XU_CLIENT = "topic.face.xu.client"; //炫优人脸识别一体机 - topic - client

	//声明队列
	@Bean
	public Queue queueOfXUFaceServer() {
		return new Queue(TOPIC_FACE_XU_SERVER,true);
	}
	@Bean
	public Queue queueOfXUFaceClient() {
		return new Queue(TOPIC_FACE_XU_CLIENT,true);
	}
	//声明交换机
	@Bean
	TopicExchange topicExOfXUFace() {
		return new TopicExchange(EX_FACE_XU);
	}
	//队列绑定交换机
	@Bean
	Binding bindingExchangeMessage1() {
		return BindingBuilder.bind(queueOfXUFaceServer()).to(topicExOfXUFace()).with(TOPIC_FACE_XU_SERVER);
	}
	//队列绑定交换机
	@Bean
	Binding bindingExchangeMessage2() {
		return BindingBuilder.bind(queueOfXUFaceClient()).to(topicExOfXUFace()).with(TOPIC_FACE_XU_CLIENT);
	}





	//炫优延时交换机名称
	public final static String DELAY_EX_FACE_XU = "DelayExOfXUFace"; //炫优人脸识别一体机 - 延时交换机

	//绑定topic名称
	public final static String DELAY_FACE_XU_SERVER = "delay.face.xu.server"; //炫优人脸识别一体机 - 延时 - server

	//声明延时交换机
	@Bean(DELAY_EX_FACE_XU)
	CustomExchange delayExchangeExOfXUFace() {
		Map<String, Object> args = new HashMap<>(1);
		args.put("x-delayed-type", "direct");
		return new CustomExchange(DELAY_EX_FACE_XU, "x-delayed-message", true, false, args);
	}

	//声明延时队列
	@Bean(DELAY_FACE_XU_SERVER)
	public Queue delayQueueOfXUFaceServer() {
		return new Queue(DELAY_FACE_XU_SERVER, true);
	}

	//延时队列绑定延时交换机
	@Bean
	Binding bindingExchangeMessage3(@Qualifier(DELAY_FACE_XU_SERVER) Queue queue,
									@Qualifier(DELAY_EX_FACE_XU) Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(DELAY_FACE_XU_SERVER).noargs();
	}

}
