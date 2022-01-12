package com.jsy.community.config;

import com.jsy.community.task.CebBankQueryTask;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Pipi
 * @Description: 定时任务
 * @Date: 2022/1/11 10:06
 * @Version: 1.0
 **/
@Configuration
public class TimingTaskConfig {

    /**
     * @author: Pipi
     * @description: 创建查询生活缴费账单任务
     * @param :
     * @return: {@link JobDetail}
     * @date: 2022/1/11 11:03
     **/
    @Bean
    public JobDetail cebBankQueryTaskDetail() {
        return JobBuilder.newJob(CebBankQueryTask.class)
                .withIdentity("CebBankQueryTask")
                //每个JobDetail内都有一个Map，包含了关联到这个Job的数据，在Job类中可以通过context获取
                .usingJobData("msg", "Hello Quartz")
                .storeDurably()
                .build();
    }

    /**
     * @author: Pipi
     * @description: 执行查询生活缴费账单任务
     * 每三天凌晨00:10分执行一次
     * @param :
     * @return: {@link Trigger}
     * @date: 2022/1/11 11:10
     **/
    @Bean
    public Trigger cebBankQueryTaskTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 08 * * * ? *");
        return TriggerBuilder.newTrigger()
                .forJob(cebBankQueryTaskDetail())
                .withIdentity("cebBankQueryTaskTrigger")
                .withSchedule(cronScheduleBuilder)
                .build();
    }
}
