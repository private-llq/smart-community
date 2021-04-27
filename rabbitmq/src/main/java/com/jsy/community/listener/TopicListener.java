package com.jsy.community.listener;

import com.jsy.community.config.TopicExConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author chq459799974
 * @description topic交换机监听队列
 * @since 2021-02-02 15:15
 **/
//@Component
public class TopicListener {
//	@RabbitHandler
//	@RabbitListener(queues = TopicExConfig.TOPIC_FACE_XU)
//	public void process1(Map testMessage) {
//		System.out.println("监听到队列topic消息(" + TopicExConfig.TOPIC_FACE_XU +") : " + testMessage.toString());
//	}

//	@RabbitListener(queues = TopicExConfig.TOPIC_FACE_XU)
//	public void process1(Map testMessage, Message message, Channel channel) throws IOException {
//		System.out.println("监听到队列topic消息(" + TopicExConfig.TOPIC_FACE_XU +") : " + testMessage.toString());
//		//手动确认
//		channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//	}

	
//	@RabbitHandler
//	@RabbitListener(queues = "topic.t1")
//	public void process1(Map testMessage) {
//		System.out.println("收到topic消息(topic.t1)  : " + testMessage.toString());
//	}
//	@RabbitHandler
//	@RabbitListener(queues = "topic.t2")
//	public void process2(Map testMessage) {
//		System.out.println("收到topic消息(topic.t2)  : " + testMessage.toString());
//	}
}
