package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.intercepter.AuthorizationInterceptor;
import com.jsy.community.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

/**
 * @author chq459799974
 * @since 2020-12-03 10:29
 **/
@Component
public class UserUtils {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	public UserInfoVo getUserInfo(String loginToken) {
		if(StringUtils.isEmpty(loginToken)){
			return null;
		}
		String str = null;
		try {
			str = stringRedisTemplate.opsForValue().get("Login:" + loginToken);
		} catch (Exception e) {
			throw new JSYException(JSYError.INTERNAL.getCode(),"redis超时");
		}
		UserInfoVo user = JSONObject.parseObject(str, UserInfoVo.class);
		return user;
	}
	
	public static UserInfoVo getUserInfo() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
			.getRequest();
		return (UserInfoVo)request.getAttribute(AuthorizationInterceptor.USER_INFO);
	}
	
	public static String getUserId() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
			.getRequest();
		return (String) request.getAttribute(AuthorizationInterceptor.USER_KEY);
	}
	
}
