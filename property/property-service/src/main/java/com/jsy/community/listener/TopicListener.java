package com.jsy.community.listener;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.*;
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

	@Resource
	private CommunityHardWareService communityHardWareService;

	@Resource
	private PeopleHistoryService peopleHistoryService;

	@Resource
	private StrangerRecordService strangerRecordService;

	//监听来自小区的访客记录新增需求
	@RabbitListener(queues = "${rabbit-mq-name.topicFaceXuClient}")
	public void addVisitorRecord(String msg, Message message, Channel channel) throws IOException {
		log.info("监听到来自小区的访客进出记录新增topic消息");
		try {
			JSONObject jsonObject = JSONObject.parseObject(msg);
			log.info("解析成功：");
			switch (jsonObject.getString("op")){
				case "visitorSync":
					//访客记录同步
					peopleHistoryService.batchAddPeopleHistory(jsonObject.getString("data"), jsonObject.getLong("communityId"));
					break;
				case "StrangerPush":
					//陌生人脸推送
					strangerRecordService.batchAddStrangerRecord(jsonObject.getString("data"));
					break;
			}
			visitorService.addVisitorRecordBatch(jsonObject);
		}catch (Exception e){
			log.error("批量新增记录失败");
			e.printStackTrace();
			//手动确认
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		}
		//手动确认
		channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
	}
	
	/**
	 * 小区消息监听
	 */
	@RabbitListener(queues = "${rabbit-mq-name.queueFromCommunity}")
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

	/**
	 * @author: Pipi
	 * @description: 专门监听扫脸机上下线状态的
	 * @param mapStr:
     * @param message:
     * @param channel:
	 * @return: void
	 * @date: 2021/8/18 17:36
	 **/
	@RabbitListener(queues = "${rabbit-mq-name.topicFaceXuClient}")
	public void listeningToOnlineStatus(String mapStr, Message message, Channel channel) throws IOException  {
		try {
			log.info("监听到小区设备状态消息: {}", mapStr);
			JSONObject jsonObject = JSONObject.parseObject(mapStr);
			log.info("解析成功：" + jsonObject);
			communityHardWareService.updateOnlineStatus(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("小区设备状态消息监听发生异常，线程号： " + Thread.currentThread().getId());
			log.info("收到的消息内容为: {}", mapStr);
			//手动确认
			channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
		}
		//手动确认
		channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
	}

}
