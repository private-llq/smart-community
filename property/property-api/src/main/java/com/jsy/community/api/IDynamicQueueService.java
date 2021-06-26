package com.jsy.community.api;

import org.springframework.amqp.core.TopicExchange;

/**
 * @author chq459799974
 * @description 动态队列相关
 * @since 2021-06-26 14:38
 **/
public interface IDynamicQueueService {
	
	/**
	 * @Description: 动态创建MQ队列并绑定社区交换机  如：大后台添加一个新小区
	 * @Param: [queueName, topicExchange]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/6/26
	 **/
	void createMQIfNotExist(String queueName, TopicExchange topicExchange);
}
