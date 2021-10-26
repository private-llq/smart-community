package com.jsy.community.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.entity.sys.SysUserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author chq459799974
 * @description 大后台Redis工具类
 * @since 2020-12-12 15:28
 **/
@Component
public class RedisUtils {
	
	public static final String USER_KEY = "userId";
	public static final String USER_INFO = "userInfo";
	
	@Value("${loginExpireHour}")
	private long loginExpireHour;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	* @Description: 设置用户登录token
	 * @Param: [token, sysUserEntity]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	public void setUserToken(String token, SysUserEntity sysUserEntity){
		stringRedisTemplate.opsForValue().set("Sys:Login:" + token, JSON.toJSONString(sysUserEntity), loginExpireHour, TimeUnit.HOURS);
		stringRedisTemplate.opsForValue().set("Sys:LoginAccount:" + sysUserEntity.getMobile(), token, loginExpireHour, TimeUnit.HOURS);
	}
	
	/**
	* @Description: 从redis获取用户信息
	 * @Param: [loginToken]
	 * @Return: com.jsy.community.entity.sys.SysUserEntity
	 * @Author: chq459799974
	 * @Date: 2020/12/12
	**/
	public SysUserEntity getUserInfo(String loginToken) {
		if(StringUtils.isEmpty(loginToken)){
			return null;
		}
		String str = null;
		try {
			str = stringRedisTemplate.opsForValue().get("Sys:Login:" + loginToken);
		} catch (Exception e) {
			throw new JSYException(JSYError.INTERNAL.getCode(),"redis超时");
		}
		SysUserEntity user = JSONObject.parseObject(str, SysUserEntity.class);
		return user;
	}
	
	/**
	* @Description: 从redis获取uid
	 * @Param: [loginToken]
	 * @Return: java.lang.Long
	 * @Author: chq459799974
	 * @Date: 2020/12/12
	**/
	public Long getUid(String loginToken) {
		SysUserEntity userInfo = getUserInfo(loginToken);
		if(userInfo != null){
			return userInfo.getId();
		}
		return null;
	}
	
	/**
	* @Description: 从redis获取用户权限
	 * @Param: [loginToken]
	 * @Return: java.util.List<java.lang.Integer>
	 * @Author: chq459799974
	 * @Date: 2020/12/12
	**/
	public List<Integer> getRoleIdList(String loginToken){
		SysUserEntity userInfo = getUserInfo(loginToken);
		if(userInfo != null){
			return userInfo.getRoleIdList();
		}
		return null;
	}
	
	/**
	* @Description: 从request域获取uid
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2020/12/12
	**/
	public static String getUid() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
			.getRequest();
		return (String) request.getAttribute(USER_KEY);
	}
}
