package com.jsy.community.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author chq459799974
 * @description 图片验证码kaptcha要求的实例Bean(暂时加上解决启动报错，待修改)
 * @since 2020-11-24 10:43
 **/
@Configuration
public class KaptchaConfig {
	@Bean
	public DefaultKaptcha producer() {
		Properties properties = new Properties();
//		properties.put("xxxx", "xxx");
		Config config = new Config(properties);
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}
}
