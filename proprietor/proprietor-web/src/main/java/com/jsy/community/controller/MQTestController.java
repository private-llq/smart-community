package com.jsy.community.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        amqpTemplate.convertAndSend("queue.sms","丢雷老母");
    }
    @GetMapping("/email")
    @ApiOperation("测试二")
    public void email(){
        Map<Object, Object> map = new HashMap<>();
        map.put("wocao","啊啊啊啊啊");
        map.put("aaa","wwwwww");
        amqpTemplate.convertAndSend("queue.email",map);
    }
}
