package com.jsy.community.service.impl;

import com.jsy.community.api.ICommonService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * 公共的
 *
 * @author ling
 * @since 2020-11-13 14:59
 */
@DubboService(version = Const.version, group = Const.group)
public class CommonServiceImpl implements ICommonService {
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public void checkVerifyCode(String account, String code) {
		String oldCode = redisTemplate.opsForValue().get(account);
		if (oldCode == null) {
			throw new ProprietorException("验证码已失效");
		}
		
		if (!oldCode.equals(code)) {
			throw new ProprietorException("验证码错误");
		}
		
		// 验证通过后删除验证码
		redisTemplate.delete(account);
	}
}
