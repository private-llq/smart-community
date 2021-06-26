package com.jsy.community.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chq459799974
 * @description topic交换机配置
 * @since 2021-02-02 15:08
 **/
@Configuration
public class TopicExConfig {
	
	//摄像头相关-交换机名称
	public final static String EX_HK_CAMERA = "topicExOfHKCamera";
	//社区相关-topic交换机名称
	public final static String EX_TOPIC_COMMUNITY = "communityTopicExchange";
	
	
	//绑定topic名称
	public final static String TOPIC_HK_CAMERA_OP = "topic.hk.camera.server.op"; //海康摄像机服务端增删改等操作(绑定用)(保证执行顺序云端现仅使用一条队列)
	public final static String TOPIC_HK_CAMERA_FLUSH = "topic.hk.camera.server.flush"; //海康摄像机服务端刷新等操作(绑定用)
	public final static String TOPIC_HK_CAMERA_SYNC_FACE = "topic.hk.camera.server.sync.face"; //海康摄像机服务端同步人脸库操作(绑定用)
	//社区相关-队列名称
	public final static String QUEUE_COMMUNITY = "queue.community"; //小区队列前缀(需根据社区id加后缀动态创建)
	
	//监听topic名称
	public final static String TOPIC_HK_CAMERA_ADD_RESULT = "topic.hk.camera.server.add.result"; //海康摄像机服务端添加结果反馈(监听用)
	public final static String TOPIC_HK_CAMERA_UPDATE_RESULT = "topic.hk.camera.server.update.result"; //海康摄像机服务端修改结果反馈(监听用)
	public final static String TOPIC_HK_CAMERA_FLUSH_RESULT = "topic.hk.camera.server.flush.result"; //海康摄像机服务端刷新结果反馈(监听用)
	public final static String TOPIC_HK_CAMERA_SYNC_FACE_RESULT = "topic.hk.camera.server.sync.face.result"; //海康摄像机服务端同步人脸库操作结果返回(监听用)
	
	//摄像头相关-声明队列
	@Bean
	public Queue queueOfHKCameraOP() {
		return new Queue(TOPIC_HK_CAMERA_OP,true);
	}
	@Bean
	public Queue queueOfHKCameraFlush() {
		return new Queue(TOPIC_HK_CAMERA_FLUSH,true);
	}
	@Bean
	public Queue queueOfHKCameraSyncFace() {
		return new Queue(TOPIC_HK_CAMERA_SYNC_FACE,true);
	}
	//摄像头相关-声明交换机
	@Bean
	TopicExchange topicExOfHKCamera() {
		return new TopicExchange(EX_HK_CAMERA);
	}
	//摄像头相关-队列绑定交换机
	@Bean
	Binding bindingExchangeOfHKCameraOP() {
		return BindingBuilder.bind(queueOfHKCameraOP()).to(topicExOfHKCamera()).with(TOPIC_HK_CAMERA_OP);
	}
	//摄像头相关-队列绑定交换机
	@Bean
	Binding bindingExchangeOfHKCameraFlush() {
		return BindingBuilder.bind(queueOfHKCameraFlush()).to(topicExOfHKCamera()).with(TOPIC_HK_CAMERA_FLUSH);
	}
	//摄像头相关-队列绑定交换机
	@Bean
	Binding bindingExchangeOfHKCameraSyncFace() {
		return BindingBuilder.bind(queueOfHKCameraSyncFace()).to(topicExOfHKCamera()).with(TOPIC_HK_CAMERA_SYNC_FACE);
	}
	
	//访客相关-声明交换机
	@Bean
	TopicExchange topicExOfProperty() {
		return new TopicExchange(RabbitMQCommonConfig.EX_PROPERTY);
	}
	//访客相关-声明队列
	@Bean
	public Queue queueOfPropertyVisitor() {
		return new Queue(RabbitMQCommonConfig.TOPIC_PROPERTY_VISITOR_RECORD,true);
	}
	//访客相关-队列绑定交换机
	@Bean
	Binding bindingOfVisitorRecord() {
		return BindingBuilder.bind(queueOfPropertyVisitor()).to(topicExOfProperty()).with(RabbitMQCommonConfig.TOPIC_PROPERTY_VISITOR_RECORD);
	}
	
	//社区相关-声明交换机
	@Bean
	TopicExchange topicExOfCommunity() {
		return new TopicExchange(EX_TOPIC_COMMUNITY);
	}
	//社区相关-声明队列
	@Bean
	public Queue queueOfCommunity() {
		return new Queue(QUEUE_COMMUNITY,true);
	}
	//社区相关-队列绑定交换机
	@Bean
	Binding bindingOfTopicExAndCommunityQueue() {
		return BindingBuilder.bind(queueOfCommunity()).to(topicExOfCommunity()).with(QUEUE_COMMUNITY);
	}
}
