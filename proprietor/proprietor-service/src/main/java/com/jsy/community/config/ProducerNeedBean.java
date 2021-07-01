package com.jsy.community.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author chq459799974
 * @description 必须要写一个Bean，注入RabbitTemplate这个Bean，否则项目无法启动，暂不知道原因
 * 相关类：RabbitmqConfig  DynamicQueueServiceImpl ，引入项目后，产生上述问题
 * @since 2021-06-23 15:04
 **/
@Component
public class ProducerNeedBean {

	@Autowired
	private RabbitTemplate rabbitTemplate;

}
