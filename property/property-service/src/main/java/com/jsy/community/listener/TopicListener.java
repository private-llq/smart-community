package com.jsy.community.listener;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.config.RabbitMQCommonConfig;
import com.jsy.community.config.TopicExConfig;
import com.jsy.community.entity.VisitorHistoryEntity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

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
	
	@Resource
	private IFacilityService facilityService;

	//监听来自APP的访客记录新增需求
	@RabbitListener(queues = RabbitMQCommonConfig.TOPIC_PROPERTY_VISITOR_RECORD)
	public void addVisitorRecord(VisitorHistoryEntity historyEntity, Message message, Channel channel) throws IOException {
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
	
	/**
	 * 小区消息监听
	 */
	@RabbitListener(queues = TopicExConfig.QUEUE_FROM_COMMUNITY)
	public void msgFromCommunity(String mapStr, Message message, Channel channel) throws IOException {
		log.info("监听到小区消息: \n" + mapStr);
		try {
			JSONObject jsonObject = JSONObject.parseObject(mapStr);
			log.info("解析成功：" + jsonObject);
			//判断操作
			switch (jsonObject.getString("act")){
				case "HKCamera":
					//海康摄像机
					facilityService.dealResultFromCommunity(jsonObject);
					break;
				case "XUFace":
					//炫优人脸识别一体机
					//TODO
					
					break;
				default:
					log.error("监听到云端到无效指令：" + jsonObject.getString("act"));
					break;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error("小区消息监听发生异常，线程号： " + Thread.currentThread().getId());
			log.info("收到的消息内容为: \n" + mapStr);
			//手动确认
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		}
		//手动确认
		channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
	}

}
