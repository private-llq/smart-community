package com.jsy.community.listener;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.config.ProprietorTopicNameEntity;
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
	
	@RabbitListener(queues = "${rabbit-mq-name.queueVisitorHisFromCommunity}")
	public void syncVisitorHistory(String msg, Message message, Channel channel) throws IOException {
		log.info("监听到访客进出记录定时同步消息: " + msg);
		try {
			JSONObject jsonObject = JSONObject.parseObject(msg);
			log.info("解析成功：" + jsonObject);
			System.out.println(jsonObject);
		}catch(Exception e){
			e.printStackTrace();
			log.error("访客进出记录定时同步消息 监听发生异常：\n" + msg);
			//手动确认
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		}
		//手动确认
		channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
	}
	
}
