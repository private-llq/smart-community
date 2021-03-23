package com.jsy.community.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @program: com.jsy.community
 * @description:  每天0点重置redis中complain_serial_number的值为1
 * @author: Hu
 * @create: 2021-03-18 14:58
 **/
@Component
public class DateTimeJob extends QuartzJobBean{
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        redisTemplate.opsForValue().set("complain_serial_number",1+"");
        System.out.println("complain_serial_number的值被重置了");
    }
}
