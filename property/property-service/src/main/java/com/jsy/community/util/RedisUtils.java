package com.jsy.community.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.UserUtils;
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
 * @description 物业端Redis工具类
 * @since 2020-12-12 15:28
 **/
@Component
public class RedisUtils {
	
	@Value("${propertyLoginExpireHour}")
	private long loginExpireHour = 12;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	* @Description: 设置用户登录token
	 * @Param: [token, sysUserEntity]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	public void setUserToken(String token, AdminUserEntity sysUserEntity){
		stringRedisTemplate.opsForValue().set("Admin:Login:" + token, JSON.toJSONString(sysUserEntity), loginExpireHour, TimeUnit.HOURS);//登录token
		stringRedisTemplate.opsForValue().set("Admin:LoginAccount:" + sysUserEntity.getMobile(), token, loginExpireHour, TimeUnit.HOURS);//登录账户key的value设为token
	}
	
	/**
	* @Description: 从redis获取用户信息
	 * @Param: [loginToken]
	 * @Return: com.jsy.community.entity.sys.SysUserEntity
	 * @Author: chq459799974
	 * @Date: 2020/12/12
	**/
	public AdminUserEntity getUserInfo(String loginToken) {
		if(StringUtils.isEmpty(loginToken)){
			return null;
		}
		String str = null;
		try {
			str = stringRedisTemplate.opsForValue().get("Admin:Login:" + loginToken);
		} catch (Exception e) {
			throw new JSYException(JSYError.INTERNAL.getCode(),"redis超时");
		}
		AdminUserEntity user = JSONObject.parseObject(str, AdminUserEntity.class);
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
		AdminUserEntity userInfo = getUserInfo(loginToken);
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
		AdminUserEntity userInfo = getUserInfo(loginToken);
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
//	public static String getUid() {
//		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
//			.getRequest();
//		return (String) request.getAttribute(UserUtils.USER_KEY);
//	}
}
