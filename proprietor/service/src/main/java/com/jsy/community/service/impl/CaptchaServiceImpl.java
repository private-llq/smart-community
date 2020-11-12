package com.jsy.community.service.impl;

import com.jsy.community.api.ICaptchaService;
import com.jsy.community.constant.Const;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 验证码
 *
 * @author ling
 * @date 2020-11-12 10:39
 */
@DubboService(version = Const.version, group = Const.group)
@RefreshScope
public class CaptchaServiceImpl implements ICaptchaService {
	
	@Value(value = "${jsy.mobileCodeExpiredTime}")
	private Integer mobileExpiredTime;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public boolean sendMobile(String mobile) {
		// 验证码暂时固定111111
		String code = "111111";
		
		// 5分钟有效期
		redisTemplate.opsForValue().set(mobile, code, mobileExpiredTime, TimeUnit.MINUTES);
		
		return true;
	}
	
	@Override
	public boolean sendEmail(String email) {
		return false;
	}
}
