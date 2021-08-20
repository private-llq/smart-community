package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.vo.ControlVO;
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
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author chq459799974
 * @since 2020-12-03 10:29
 **/
@Component
public class UserUtils {
	
	public static final String USER_TOKEN = "token";
	public static final String USER_KEY = "userId";
	public static final String USER_INFO = "userInfo";
	public static final String USER_COMMUNITY = "communityId";
	public static final String USER_COMMUNITY_ID_LIST = "communityIds";
	public static final String USER_COMPANY_ID = "companyId";
	public static final String USER_ROLE_ID = "roleId";

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
		String str;
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
	public static UserInfoVo getUserInfo() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
			.getRequest();
		return (UserInfoVo)request.getAttribute(USER_INFO);
	}


	
	/**
	 * @Description: 获取request域中用户信息(登录用户自己的信息)
	 * @Param: []
	 * @Return: com.jsy.community.vo.UserInfoVo
	 * @Author: chq459799974
	 * @Date: 2020/12/3
	 **/
	public static AdminInfoVo getAdminUserInfo() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
			.getRequest();
		return (AdminInfoVo)request.getAttribute(USER_INFO);
	}
	
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
	* @Description: 获取request域中用户token
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/8
	**/
	public static String getUserToken() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
			.getRequest();
		return (String) request.getAttribute(USER_TOKEN);
	}
	
	/**
	* @Description: 获取物业端登录用户社区ID
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	public static Long getAdminCommunityId() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
			.getRequest();
		return (Long) request.getAttribute(USER_COMMUNITY);
	}

	/**
	 * @author: Pipi
	 * @description: 获取物业端登陆用户物业公司ID
	 * @param :
	 * @return: java.lang.Long
	 * @date: 2021/7/29 11:20
	 **/
	public static Long getAdminCompanyId() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
				.getRequest();
		return (Long) request.getAttribute(USER_COMPANY_ID);
	}

	/**
	 * @author: Pipi
	 * @description: 获取物业端登陆用户角色ID
	 * @param :
	 * @return: java.lang.Long
	 * @date: 2021/8/6 14:28
	 **/
	public static Long getAdminRoleId() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
				.getRequest();
		return (Long) request.getAttribute(USER_ROLE_ID);
	}
	
	/**
	 * @Description: 获取物业端登录用户有权限的社区IDList
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/7/20
	 **/
	public static List<Long> getAdminCommunityIdList() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
			.getRequest();
		return (List<Long>) request.getAttribute(USER_COMMUNITY_ID_LIST);
	}
	
	/**
	* @Description: 校验当前登录用户小区权限
	 * @Param: [communityIdStr]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/7/20
	**/
	public static void validateCommunityId(Long communityId){
		List<Long> communityIdList = getAdminCommunityIdList();
		if(!communityIdList.contains(communityId)){
			throw new JSYException(JSYError.BAD_REQUEST.getCode(),"无该社区权限！");
		}
	}
	
	/**
	 * @Description: 校验当前登录用户小区权限
	 * @Param: [communityIdStr]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/7/20
	 **/
	public static void validateCommunityIds(Collection<Long> communityIds){
		List<Long> communityIdList = getAdminCommunityIdList();
		for(Long communityId : communityIds){
			if(!communityIdList.contains(communityId)){
				throw new JSYException(JSYError.BAD_REQUEST.getCode(),"部分社区无权限！");
			}
		}
	}
	
	/**
	* @Description: 生成用户token(目前是uuid)
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2020/12/3
	**/
	public static String randomUUID(){
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
		String userToken = randomUUID();
		redisTemplate.opsForValue().set(typeName + ":" + userToken,o);
		return userToken;
	}

	/**
	 * @author: Pipi
	 * @description: 更新token内容
	 * @param typeName: token类型
     * @param o: token对应值
	 * @return: void
	 * @date: 2021/7/22 15:04
	 **/
	public void updateRedisByToken(String typeName, Object o, String token, Long expireTime) {
		redisTemplate.opsForValue().set(typeName + ":" + token, o, expireTime ,TimeUnit.HOURS);
	}

	/**
	* @Description: 设置token(带过期时间)
	 * @Param: [typeName, o, time, timeUnit]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2020/12/4
	**/
	public String setRedisTokenWithTime(String typeName,Object o,long time,TimeUnit timeUnit){
		String userToken = randomUUID();
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
        return result1 || result2;
    }


	/**
	 * @Description: 获取当前小区权限
	 * @author: Hu
	 * @since: 2021/8/17 9:11
	 * @Param: null
	 * @return:
	 */
	public static ControlVO getPermissions(String uid, RedisTemplate redisTemplate) {
		String str;
		try {
			str=(String) redisTemplate.opsForValue().get("Permissions:" + uid);
		} catch (Exception e) {
			throw new JSYException(JSYError.INTERNAL.getCode(),"redis超时");
		}
		ControlVO controlVO = JSONObject.parseObject(str, ControlVO.class);
		return controlVO;
	}
}
