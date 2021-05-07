package com.jsy.community.listener;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.config.RabbitMQConfig;
import com.jsy.community.config.TopicExConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorHistoryEntity;
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

	//监听来自APP的访客记录新增需求
	@RabbitListener(queues = RabbitMQConfig.TOPIC_PROPERTY_VISITOR_RECORD)
	public void process1(VisitorHistoryEntity historyEntity, Message message, Channel channel) throws IOException {
		log.info("监听到来自APP的访客记录新增topic消息: " + historyEntity.toString());
		try {
			boolean b = visitorService.addVisitorRecord(historyEntity);
			if(!b){
				log.error("新增访客记录失败：" + historyEntity);
			}
		}catch (Exception e){
			log.error("新增访客记录失败：" + historyEntity);
			e.printStackTrace();
		}
		//手动确认
		channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
	}

}
