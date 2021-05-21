package com.jsy.community.config;

import org.springframework.context.annotation.Configuration;

/**
 * @author chq459799974
 * @description Rabbit-MQ 公共配置参数同步
 * @since 2021-05-06 16:19
 **/
@Configuration
public class RabbitMQCommonConfig {
	
	//交换机名称
	public final static String EX_PROPERTY = "topicExOfProperty"; //物业端 - 交换机
	
	//物业端访客记录路由键
	public final static String TOPIC_PROPERTY_VISITOR_RECORD = "topic.property.visitor.record"; //物业端访客记录路由键
	
}
