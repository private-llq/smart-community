package com.jsy.community.controller;

import com.jsy.community.config.TopicExConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author chq459799974
 * @description TODO
 * @since 2021-02-02 13:40
 **/
@RestController
public class TestController {
	@Autowired
	private RabbitTemplate rabbitTemplate;
//	@RequestMapping("testSendDirect")
//	public void testSendDirect(){
//		HashMap<String, Object> map = new HashMap<>();
//		map.put("messageId", UUID.randomUUID().toString().replace("-",""));
//		map.put("name", "张三");
//		map.put("age", 22);
//		rabbitTemplate.convertAndSend("directEx1","directKey1",map);
//	}
	@RequestMapping("testSendTopic")
	public void testSendTopic(){
		HashMap<String, Object> map = new HashMap<>();
		map.put("messageId", UUID.randomUUID().toString().replace("-",""));
		map.put("name", "张三");
		map.put("age", 22);
		rabbitTemplate.convertAndSend(TopicExConfig.EX_FACE_XU,TopicExConfig.TOPIC_FACE_XU,map);
		
//		HashMap<String, Object> map2 = new HashMap<>();
//		map2.put("messageId", UUID.randomUUID().toString().replace("-",""));
//		map2.put("name", "李四");
//		map2.put("age", 22);
//		rabbitTemplate.convertAndSend("topicEx1","topic.t2",map2);
	}
}
