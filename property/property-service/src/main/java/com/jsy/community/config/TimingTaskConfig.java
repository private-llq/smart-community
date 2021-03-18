package com.jsy.community.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @program: com.jsy.community
 * @description: 定时任务
 * @author: Hu
 * @create: 2021-03-18 09:59
 **/
@Component
public class TimingTaskConfig {

    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 0 * * ? *")
    public void scheduledTask1(){
        redisTemplate.opsForValue().set("complain_serial_number","1");
        System.out.println("重置complain_serial_number了为1");
    }

    @Scheduled(cron = "0 * * * * ? *")
    public void scheduledTask2(){
        redisTemplate.opsForValue().set("complain_serial_number","1");
        System.out.println("分钟：重置complain_serial_number了为1");
    }
}
