package com.jsy.community.config;

import com.jsy.community.job.DateTimeJob;
import com.jsy.community.job.FinanceBillJob;
import com.jsy.community.job.FinanceStatementJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: com.jsy.community
 * @description: 定时任务
 * @author: Hu
 * @create: 2021-03-18 09:59
 **/
@Configuration
public class TimingTaskConfig{

    /**
     * @Description: 重置redis自然数配置
     * @author: Hu
     * @since: 2021/4/22 9:22
     * @Param:
     * @return:
     */
    @Bean
    public JobDetail printTimeJobDetail(){
        return JobBuilder.newJob(DateTimeJob.class)//PrintTimeJob我们的业务类
                .withIdentity("DateTimeJob")//可以给该JobDetail起一个id
                //每个JobDetail内都有一个Map，包含了关联到这个Job的数据，在Job类中可以通过context获取
                .usingJobData("msg", "Hello Quartz")//关联键值对
                .storeDurably()//即使没有Trigger关联时，也不需要删除该JobDetail
                .build();
    }
    /**
     * @Description: 重置redis自然数配置
     * @author: Hu
     * @since: 2021/4/22 9:22
     * @Param:
     * @return:
     */
    @Bean
    public Trigger printTimeJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 0 * * ? *");
        return TriggerBuilder.newTrigger()
                .forJob(printTimeJobDetail())//关联上述的JobDetail
                .withIdentity("quartzTaskService")//给Trigger起个名字
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    /**
     * @Description: 每天查询数据库生成财务账单或者生成违约费
     * @author: Hu
     * @since: 2021/4/22 9:22
     * @Param:
     * @return:
     */
    @Bean
    public JobDetail financeBillJobDetail(){
        return JobBuilder.newJob(FinanceBillJob.class)//PrintTimeJob我们的业务类
                .withIdentity("FinanceBillJob")//可以给该JobDetail起一个id
                //每个JobDetail内都有一个Map，包含了关联到这个Job的数据，在Job类中可以通过context获取
                .usingJobData("msg", "Hello Quartz")//关联键值对
                .storeDurably()//即使没有Trigger关联时，也不需要删除该JobDetail
                .build();
    }
    /**
     * @Description: 每天查询数据库生成财务账单或者生成违约费
     * @author: Hu
     * @since: 2021/4/22 9:22
     * @Param:
     * @return:
     */
    @Bean
    public Trigger financeBillJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 0 * * ? *");
        return TriggerBuilder.newTrigger()
                .forJob(financeBillJobDetail())//关联上述的JobDetail
                .withIdentity("financeBillTaskService")//给Trigger起个名字
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    /**
     *@Author: Pipi
     *@Description: 创建财务结算JobDetail
     *@Param: : 
     *@Return: org.quartz.JobDetail
     *@Date: 2021/4/21 14:56
     **/
    @Bean
    public JobDetail financeStatementJobDetail() {
        return JobBuilder.newJob(FinanceStatementJob.class)
                .withDescription("FinanceStatementJob")
                .usingJobData("statementJob", "statementJobQuartz")
                .storeDurably()
                .build();
    }

    /**
     *@Author: Pipi
     *@Description: 创建财务结算JobTrigger
     *@Param: :
     *@Return: org.quartz.Trigger
     *@Date: 2021/4/21 14:59
     **/
    @Bean
    public Trigger financeStatementJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 10 0 * * ? *");
        return TriggerBuilder.newTrigger()
                .forJob(financeStatementJobDetail())
                .withIdentity("financeStatementJobTrigger")
                .withSchedule(cronScheduleBuilder)
                .build();
    }
}
