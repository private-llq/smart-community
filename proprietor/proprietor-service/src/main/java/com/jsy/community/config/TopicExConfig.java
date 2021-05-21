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
	//声明交换机  TODO 人脸下发改扇形交换机？
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