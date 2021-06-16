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
	public final static String EX_HK_CAMERA = "topicExOfHKCamera"; //海康摄像头
	
	//绑定topic名称
	public final static String TOPIC_HK_CAMERA_OP = "topic.hk.camera.server.op"; //海康摄像机服务端增删改等操作(绑定用)(保证执行顺序云端现仅使用一条队列)
	
	//监听topic名称
	public final static String TOPIC_HK_CAMERA_ADD_RESULT = "topic.hk.camera.server.add.result"; //海康摄像机服务端添加结果反馈(监听用)
	public final static String TOPIC_HK_CAMERA_UPDATE_RESULT = "topic.hk.camera.server.update.result"; //海康摄像机服务端修改结果反馈(监听用)
	
	//声明队列
	@Bean
	public Queue queueOfHKCameraOP() {
		return new Queue(TOPIC_HK_CAMERA_OP,true);
	}
	
	//声明交换机
	@Bean
	TopicExchange topicExOfHKCamera() {
		return new TopicExchange(EX_HK_CAMERA);
	}
	
	//队列绑定交换机
	@Bean
	Binding bindingExchangeOfHKCameraOP() {
		return BindingBuilder.bind(queueOfHKCameraOP()).to(topicExOfHKCamera()).with(TOPIC_HK_CAMERA_OP);
	}
	
	
	//声明交换机
	@Bean
	TopicExchange topicExOfProperty() {
		return new TopicExchange(RabbitMQCommonConfig.EX_PROPERTY);
	}
	//声明队列
	@Bean
	public Queue queueOfPropertyVisitor() {
		return new Queue(RabbitMQCommonConfig.TOPIC_PROPERTY_VISITOR_RECORD,true);
	}
	//队列绑定交换机
	@Bean
	Binding bindingOfVisitorRecord() {
		return BindingBuilder.bind(queueOfPropertyVisitor()).to(topicExOfProperty()).with(RabbitMQCommonConfig.TOPIC_PROPERTY_VISITOR_RECORD);
	}

}
