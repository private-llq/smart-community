package com.jsy.community.config;

import com.jsy.community.job.TimeTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author lihao
 * @ClassName QuartzConfig
 * @Date 2020/12/3  11:11
 * @Description TODO
 * @Version 1.0
 **/
@Configuration
public class QuartzConfig {
	
	@Bean
	public JobDetailFactoryBean jobDetailFactoryBean() {
		JobDetailFactoryBean factory = new JobDetailFactoryBean();
		//关联我们自己的Job类
		factory.setJobClass(TimeTask.class);
		return factory;
	}
	
	@Bean
	public CronTriggerFactoryBean cronTriggerFactoryBean(JobDetailFactoryBean jobDetailFactoryBean) {
		CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
		factory.setJobDetail(jobDetailFactoryBean.getObject());
		//设置触发时间
//		factory.setCronExpression("0 0/4 * * * ? *"); // TODO 暂定
		factory.setCronExpression("0/2 * * * * ?");
//		factory.setCronExpression("0 0 0 * * ? *");
		return factory;
	}
	
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(CronTriggerFactoryBean cronTriggerFactoryBean, MyAdaptableJobFactory jobFactory) {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		//关联trigger
		factory.setTriggers(cronTriggerFactoryBean.getObject());
		factory.setJobFactory(jobFactory);
		return factory;
	}
}
