package com.jsy.community.service.impl;

import com.jsy.community.api.ICommunityService;
import com.jsy.community.api.IDynamicQueueService;
import com.jsy.community.config.PropertyTopicNameEntity;
import com.jsy.community.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Properties;

/**
 * @author chq459799974
 * @description 动态队列相关
 * @since 2021-06-26 10:18
 **/
@Slf4j
@Service
public class DynamicQueueServiceImpl implements IDynamicQueueService {
	
	@Autowired
	private RabbitAdmin rabbitAdmin;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommunityService communityService;
	
	/**
	* @Description: 项目启动自动任务，创建小区队列，便于MQ向小区下发指令
	 * @Param: []
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/6/26
	**/
	// @PostConstruct该注解被用来修饰一个非静态的void（）方法。被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，
	// 并且只会被服务器执行一次。PostConstruct在构造函数之后执行，init（）方法之前执行
	// @PostConstruct
	public void createCommunityQueues(){
		//获取所有小区ID
		List<Long> communityIds = communityService.queryAllCommunityIdList();
		if(CollectionUtils.isEmpty(communityIds)){
			return;
		}
		for(Long communityId : communityIds){
			//创建小区队列
			createMQIfNotExist(PropertyTopicNameEntity.queueToCommunity + "." + communityId,new TopicExchange(PropertyTopicNameEntity.exTopicToCommunity));
		}
	}
	
	/**
	* @Description: 动态创建MQ队列并绑定社区交换机 场景1.项目启动自动任务 场景2.大后台添加一个新小区
	 * @Param: [queueName, topicExchange]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/6/26
	**/
	@Override
	public void createMQIfNotExist(String queueName, TopicExchange topicExchange){
		Properties properties = rabbitAdmin.getQueueProperties(queueName);
		if(properties == null){
			log.info("队列不存在，开始创建队列queueName..." + queueName + "并绑定topic交换机：" + topicExchange.getName());
			Queue queue = new Queue(queueName,true,false,false,null);
//			TopicExchange topicExchange = new TopicExchange(exchangeName);
			rabbitAdmin.declareQueue(queue);
//			rabbitAdmin.declareExchange(topicExchange);
			rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(topicExchange).with(queueName));
		}
	}
}
