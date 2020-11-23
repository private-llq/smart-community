package com.jsy.community.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Locale;


/**
 * spring 工具类
 *
 * @author ling
 * @since 2020-11-19 10:09
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
	
	private static ApplicationContext context = null;
	
	@Override
	public void setApplicationContext(@Nonnull ApplicationContext applicationContext)
		throws BeansException {
		context = applicationContext;
	}
	
	// 传入线程中
	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}
	
	// 国际化使用
	public static String getMessage(String key) {
		return context.getMessage(key, null, Locale.getDefault());
	}
	
	/// 获取当前环境
	public static String getActiveProfile() {
		return context.getEnvironment().getActiveProfiles()[0];
	}
}