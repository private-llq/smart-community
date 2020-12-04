package com.jsy.community.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

@Component
public class MyAdaptableJobFactory extends AdaptableJobFactory {

	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory; // TODO 若后面要在任务类里面注入时需要这个
	
	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle)	throws Exception {
		Object obj = super.createJobInstance(bundle);
		//将obj 对象添加Spring IOC 容器中，并完成注入
		this.autowireCapableBeanFactory.autowireBean(obj);
		return obj;
	}
}