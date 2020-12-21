package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.vo.UserInfoVo;
import com.jsy.community.vo.admin.AdminInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author chq459799974
 * @since 2020-12-03 10:29
 **/
@Component
public class UserUtils {
	
	public static final String USER_KEY = "userId";
	public static final String USER_INFO = "userInfo";
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	* @Description: 通过token获取用户信息(业主端)
	 * @Param: [loginToken]
	 * @Return: com.jsy.community.vo.UserInfoVo
	 * @Author: chq459799974
	 * @Date: 2020/12/3
	**/
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
	
	/**
	* @Description: 通过token获取用户信息(物业端)
	 * @Param: [loginToken]
	 * @Return: com.jsy.community.vo.admin.AdminInfoVo
	 * @Author: chq459799974
	 * @Date: 2020/12/21
	**/
	public AdminInfoVo getAdminInfo(String loginToken) {
		if(StringUtils.isEmpty(loginToken)){
			return null;
		}
		String str = null;
		try {
			str = stringRedisTemplate.opsForValue().get("Admin:Login:" + loginToken);
		} catch (Exception e) {
			throw new JSYException(JSYError.INTERNAL.getCode(),"redis超时");
		}
		AdminInfoVo adminUser = JSONObject.parseObject(str, AdminInfoVo.class);
		return adminUser;
	}
	
	/**
	* @Description: 获取request域中用户信息(登录用户自己的信息)
	 * @Param: []
	 * @Return: com.jsy.community.vo.UserInfoVo
	 * @Author: chq459799974
	 * @Date: 2020/12/3
	**/
//	public static UserInfoVo getUserInfo() {
//		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
//			.getRequest();
//		return (UserInfoVo)request.getAttribute(USER_INFO);
//	}
	
	/**
	* @Description: 获取request域中用户id(登录用户自己的uid)
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2020/12/3
	**/
	public static String getUserId() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
			.getRequest();
		return (String) request.getAttribute(USER_KEY);
	}
	
	/**
	* @Description: 生成用户token(目前是uuid)
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2020/12/3
	**/
	public static String createUserToken(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	* @Description: 设置token
	 * @Param: [typeName, o]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2020/12/4
	**/
	public String setRedisToken(String typeName,Object o){
		String userToken = createUserToken();
		redisTemplate.opsForValue().set(typeName + ":" + userToken,o);
		return userToken;
	}
	
	/**
	* @Description: 设置token(带过期时间)
	 * @Param: [typeName, o, time, timeUnit]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2020/12/4
	**/
	public String setRedisTokenWithTime(String typeName,Object o,long time,TimeUnit timeUnit){
		String userToken = createUserToken();
		redisTemplate.opsForValue().set(typeName + ":" + userToken,o,time,timeUnit);
		return userToken;
	}
	
	/**
	* @Description: 获取token
	 * @Param: [typeName, token]
	 * @Return: java.lang.Object
	 * @Author: chq459799974
	 * @Date: 2020/12/4
	**/
	public Object getRedisToken(String typeName,String token){
		return redisTemplate.opsForValue().get(typeName + ":" + token);
	}
	
	/**
	* @Description: 销毁token
	 * @Param: [typeName, token]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/4
	**/
	public boolean destroyToken(String typeName,String token){
		Boolean result1 = redisTemplate.delete(typeName + ":" + token);
		Boolean result2 = stringRedisTemplate.delete(typeName + ":" + token);
		if(result1 || result2){
			return true;
		}
		return false;
	}
	
}
