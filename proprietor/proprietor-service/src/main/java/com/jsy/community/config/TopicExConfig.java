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
	
	//小区相关-topic交换机名称
	public final static String EX_TOPIC_VISITOR_TO_COMMUNITY = "visitorTopicExchange2Community";

	//绑定topic名称
	public final static String QUEUE_VISITOR_TO_COMMUNITY = "queue.visitor.2community"; //小区监听云端队列(根据社区id加后缀动态创建) 如小区id是1 创建出来就是queue.community.1 小区id是2 创建出来就是queue.community.2

	//监听topic名称
	public final static String QUEUE_VISITOR_HIS_FROM_COMMUNITY = "queue.visitor.his.2cloud"; //云端监听小区队列，根据参数communityId判断是哪个小区

	//小区相关-声明交换机
	@Bean
	TopicExchange topicExOfVisitorToCommunity() {
		return new TopicExchange(EX_TOPIC_VISITOR_TO_COMMUNITY);
	}
	//小区相关-声明队列
	@Bean
	public Queue queueOfVisitor2Community() {
		return new Queue(QUEUE_VISITOR_TO_COMMUNITY,true);
	}
	//小区相关-队列绑定交换机
	@Bean
	Binding bindingOfVisitorTopicExAndQueue() {
		return BindingBuilder.bind(queueOfVisitor2Community()).to(topicExOfVisitorToCommunity()).with(QUEUE_VISITOR_TO_COMMUNITY);
	}
	
	
	
	//交换机名称
	public final static String EX_FACE_XU = "topicExOfXUFace"; //炫优人脸识别一体机 - 交换机
	
	//topic名称
	public final static String TOPIC_FACE_XU_SERVER = "topic.face.xu.server"; //炫优人脸识别一体机 - topic - server
	//topic名称
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
	@Bean
	public Queue queueOfPropertyVisitor() {
		return new Queue(RabbitMQCommonConfig.TOPIC_PROPERTY_VISITOR_RECORD,true);
	}
	//声明交换机
	@Bean
	TopicExchange topicExOfXUFace() {
		return new TopicExchange(EX_FACE_XU);
	}
	@Bean
	TopicExchange topicExOfProperty() {
		return new TopicExchange(RabbitMQCommonConfig.EX_PROPERTY);
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
	//队列绑定交换机
	@Bean
	Binding bindingExchangeMessage3() {
		return BindingBuilder.bind(queueOfPropertyVisitor()).to(topicExOfProperty()).with(RabbitMQCommonConfig.TOPIC_PROPERTY_VISITOR_RECORD);
	}
	
	
	
	//绑定键
//	public final static String topic1 = "topic.t1";
//	public final static String topic2 = "topic.t2";
	
//	@Bean
//	public Queue firstQueue() {
//		return new Queue(TopicExConfig.topic1,true);
//	}
//
//	@Bean
//	public Queue secondQueue() {
//		return new Queue(TopicExConfig.topic2,true);
//	}
	
//	@Bean
//	TopicExchange topicEx1() {
//		return new TopicExchange("topicEx1");
//	}
	
	
	//将firstQueue和topicExchange绑定,而且绑定的键值为topic.t1
	//这样只要是消息携带的路由键是topic.t1,才会分发到该队列
//	@Bean
//	Binding bindingExchangeMessage() {
//		return BindingBuilder.bind(firstQueue()).to(topicEx1()).with(topic1);
//	}
	
	//将secondQueue和topicExchange绑定,而且绑定的键值为用上通配路由键规则topic.#
	// 这样只要是消息携带的路由键是以topic.开头,都会分发到该队列
//	@Bean
//	Binding bindingExchangeMessage2() {
//		return BindingBuilder.bind(secondQueue()).to(topicEx1()).with("topic.#");
//	}
	
}
