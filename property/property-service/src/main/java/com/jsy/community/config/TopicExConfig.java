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
	
	//小区相关-topic交换机名称
	public final static String EX_TOPIC_TO_COMMUNITY = "cloud2CommunityTopicExchange";
	
	//绑定topic名称
	public final static String QUEUE_TO_COMMUNITY = "queue.2community"; //小区监听云端队列(根据社区id加后缀动态创建) 如小区id是1 创建出来就是queue.community.1 小区id是2 创建出来就是queue.community.2
	
	//监听topic名称
	public final static String QUEUE_FROM_COMMUNITY = "queue.2cloud"; //云端监听小区队列，根据参数communityId判断是哪个小区(摄像头相关)
	public final static String QUEUE_VISITOR_HIS_FROM_COMMUNITY = "queue.visitor.his.2cloud"; //云端监听小区队列，根据参数communityId判断是哪个小区(访客记录相关)
	
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
}
