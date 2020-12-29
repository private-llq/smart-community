package com.jsy.community.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-17 15:44
 **/
@RestController
@Api(tags = "MQ测试")
public class MQTestController {

    @Autowired
    private AmqpTemplate amqpTemplate;


    @GetMapping("/sms")
    @ApiOperation("测试一")
    public void sms(){
        amqpTemplate.convertAndSend("exchange_topics","queue.sms","丢雷老母");
        System.out.println("mq发送消息了");
    }
    //延迟消息测试
    @GetMapping("/test")
    @ApiOperation("测试三")
    public void test(){
        System.out.println(new Date());
        amqpTemplate.convertAndSend("exchange_delay", "queue.test", "凸(艹皿艹 )", new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay",30000);
                return message;
            }
        });
        System.out.println("mq发送消息了");
    }
    @GetMapping("/email")
    @ApiOperation("测试二")
    public void email(){
        Map<Object, Object> map = new HashMap<>();
        map.put("wocao","啊啊啊啊啊");
        map.put("aaa","wwwwww");
        amqpTemplate.convertAndSend("exchange_topics","queue.email",map);
        System.out.println("mq发送消息了");
    }
}
