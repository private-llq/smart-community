package com.jsy.community.listener;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.config.RabbitMQConfig;
import com.jsy.community.config.TopicExConfig;
import com.jsy.community.constant.BusinessConst;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @author chq459799974
 * @description topic交换机监听队列
 * @since 2021-02-02 15:15
 **/
@Slf4j
@Component
public class TopicListener {
	
	@Resource
	private IVisitorService visitorService;
	
	@RabbitListener(queues = TopicExConfig.TOPIC_FACE_XU_CLIENT)
	public void process1(Map mapBody, Message message, Channel channel) throws IOException {
		log.info("监听到人脸一体机topic消息(" + TopicExConfig.TOPIC_FACE_XU_CLIENT +") : " + mapBody.toString());
		try {
			JSONObject jsonObject = JSONObject.parseObject(mapBody.toString());
			switch(jsonObject.getString("operator")) {
				case "QRCodePush":
					visitorService.verifyQRCode(jsonObject, BusinessConst.HARDWARE_TYPE_XU_FACE);
					break;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error("消息监听发生异常 " + Thread.currentThread().getId());
			log.error("监听到人脸一体机topic消息(" + TopicExConfig.TOPIC_FACE_XU_CLIENT +") : " + mapBody.toString());
			//手动确认
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		}
		//手动确认
		channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
	}
	
	
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
