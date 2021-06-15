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
	public final static String TOPIC_HK_CAMERA_ADD = "topic.hk.camera.server.add"; //海康摄像机服务端添加(绑定用)
	public final static String TOPIC_HK_CAMERA_DEL = "topic.hk.camera.server.del"; //海康摄像机服务端删除(绑定用)
	public final static String TOPIC_HK_CAMERA_UPDATE = "topic.hk.camera.server.update"; //海康摄像机服务端修改(绑定用)
	
	//监听topic名称
	public final static String TOPIC_HK_CAMERA_ADD_RESULT = "topic.hk.camera.server.add.result"; //海康摄像机服务端添加结果反馈(监听用)
	public final static String TOPIC_HK_CAMERA_DEL_RESULT = "topic.hk.camera.server.del.result"; //海康摄像机服务端删除结果反馈(监听用)
	public final static String TOPIC_HK_CAMERA_UPDATE_RESULT = "topic.hk.camera.server.update.result"; //海康摄像机服务端修改结果反馈(监听用)
	
	//声明队列
	@Bean
	public Queue queueOfHKCameraAdd() {
		return new Queue(TOPIC_HK_CAMERA_ADD,true);
	}
	//声明队列
	@Bean
	public Queue queueOfHKCameraDel() {
		return new Queue(TOPIC_HK_CAMERA_DEL,true);
	}
	//声明队列
	@Bean
	public Queue queueOfHKCameraUpdate() {
		return new Queue(TOPIC_HK_CAMERA_UPDATE,true);
	}
	
	//声明交换机
	@Bean
	TopicExchange topicExOfHKCamera() {
		return new TopicExchange(EX_HK_CAMERA);
	}
	
	//队列绑定交换机
	@Bean
	Binding bindingExchangeOfHKCameraAdd() {
		return BindingBuilder.bind(queueOfHKCameraAdd()).to(topicExOfHKCamera()).with(TOPIC_HK_CAMERA_ADD);
	}
	//队列绑定交换机
	@Bean
	Binding bindingExchangeOfHKCameraDel() {
		return BindingBuilder.bind(queueOfHKCameraDel()).to(topicExOfHKCamera()).with(TOPIC_HK_CAMERA_DEL);
	}
	//队列绑定交换机
	@Bean
	Binding bindingExchangeOfHKCameraUpdate() {
		return BindingBuilder.bind(queueOfHKCameraUpdate()).to(topicExOfHKCamera()).with(TOPIC_HK_CAMERA_UPDATE);
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
