package com.jsy.community.utils;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.vo.ControlVO;
import com.jsy.community.vo.UserInfoVo;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.sys.SysInfoVo;
import com.zhsj.baseweb.support.ContextHolder;
import com.zhsj.baseweb.support.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author chq459799974
 * @since 2020-12-03 10:29
 **/
@Component
public class UserUtils {
	
	public static final String USER_TOKEN = "token";
	public static final String USER_KEY = "userId";
	public static final String USER_ID = "id";
	public static final String USER_INFO = "userInfo";
	public static final String USER_COMMUNITY = "communityId";
	public static final String USER_COMMUNITY_ID_LIST = "communityIds";
	public static final String USER_COMPANY_ID = "companyId";
	public static final String USER_ROLE_ID = "roleId";

	private static StringRedisTemplate stringRedisTemplate;

	private static RedisTemplate redisTemplate;

	@Autowired
	public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
		UserUtils.stringRedisTemplate = stringRedisTemplate;
	}

	@Autowired
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		UserUtils.redisTemplate = redisTemplate;
	}
	
	/**
	* @Description: 通过token获取用户信息(业主端)
	 * @Param: [loginToken]
	 * @Return: com.jsy.community.vo.UserInfoVo
	 * @Author: chq459799974
	 * @Date: 2020/12/3
	**/
	public static UserInfoVo getUserInfo(String loginToken) {
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
	public static AdminInfoVo getAdminInfo() {
		String str;
		try {
			str = stringRedisTemplate.opsForValue().get("Admin:Login:" + getUserToken());
		} catch (Exception e) {
			throw new JSYException(JSYError.INTERNAL.getCode(),"redis超时");
		}
		return JSONObject.parseObject(str, AdminInfoVo.class);
	}
	
	/**
	 * @Description: 通过token获取用户信息(大后台)
	 * @Param: [loginToken]
	 * @Return: com.jsy.community.vo.sys.SysInfoVo
	 * @Author: DKS
	 * @Date: 2021/10/14
	 **/
	public static SysInfoVo getSysInfo() {
		String str;
		try {
			str = stringRedisTemplate.opsForValue().get("Sys:Login:" + getUserToken());
		} catch (Exception e) {
			throw new JSYException(JSYError.INTERNAL.getCode(),"redis超时");
		}
		return JSONObject.parseObject(str, SysInfoVo.class);
	}
	
	/**
	* @Description: 获取request域中用户信息(登录用户自己的信息)
	 * @Param: []
	 * @Return: com.jsy.community.vo.UserInfoVo
	 * @Author: chq459799974
	 * @Date: 2020/12/3
	**/
	public static UserInfoVo getUserInfo() {
//		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
//			.getRequest();
		LoginUser loginUser = ContextHolder.getContext().getLoginUser();
		UserInfoVo userInfoVo = new UserInfoVo();
		
		userInfoVo.setId(loginUser.getId());
		userInfoVo.setMobile(loginUser.getPhone());
		userInfoVo.setUid(loginUser.getAccount());
		userInfoVo.setImId(loginUser.getImId());
		userInfoVo.setNickname(loginUser.getNickName());
		return userInfoVo;
	}


	
	/**
	 * @Description: 获取request域中用户信息(登录用户自己的信息)
	 * @Param: []
	 * @Return: com.jsy.community.vo.UserInfoVo
	 * @Author: chq459799974
	 * @Date: 2020/12/3
	 **/
	public static AdminInfoVo getAdminUserInfo() {
//		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
//			.getRequest();
//		return (AdminInfoVo)request.getAttribute(USER_INFO);
		return getAdminInfo();
	}
	
	/**
	* @Description: 获取request域中用户id(登录用户自己的uid)
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2020/12/3
	**/
	public static String getUserId() {
//		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
//			.getRequest();
		return ContextHolder.getContext().getLoginUser().getAccount();
	}

	/**
	 * @author: Pipi
	 * @description: 获取e到家用户id
	 * @param :
	 * @return: {@link Long}
	 * @date: 2021/12/8 16:54
	 **/
	public static Long getEHomeUserId() {
		return ContextHolder.getContext().getLoginUser().getId();
	}
	
	/**
	 * @Description: 获取request域中用户id(登录用户自己的uid)
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: DKS
	 * @Date: 2021/10/14
	 **/
	public static String getId() {
//		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
//			.getRequest();
//		return (String) request.getAttribute(USER_ID);
		return String.valueOf(ContextHolder.getContext().getLoginUser().getId());
	}
	
	/**
	* @Description: 获取request域中用户token
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/8
	**/
	public static String getUserToken() {
		return ContextHolder.getContext().getLoginUser().getToken();
	}
	/*public static String getUserToken() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
			.getRequest();
		return (String) request.getAttribute(USER_TOKEN);
	}*/
	
	/**
	* @Description: 获取物业端登录用户社区ID
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	public static Long getAdminCommunityId() {
//		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
//			.getRequest();
//		return (Long) request.getAttribute(USER_COMMUNITY);
		AdminInfoVo adminInfo = getAdminInfo();
		return adminInfo.getCommunityId();
	}

	/**
	 * @author: Pipi
	 * @description: 获取物业端登陆用户物业公司ID
	 * @param :
	 * @return: java.lang.Long
	 * @date: 2021/7/29 11:20
	 **/
	public static Long getAdminCompanyId() {
//		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
//				.getRequest();
//		return (Long) request.getAttribute(USER_COMPANY_ID);
		AdminInfoVo adminInfo = getAdminInfo();
		return adminInfo.getCompanyId();
	}

	/**
	 * @author: Pipi
	 * @description: 获取物业端登陆用户角色ID
	 * @param :
	 * @return: java.lang.Long
	 * @date: 2021/8/6 14:28
	 **/
	public static Long getAdminRoleId() {
//		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
//				.getRequest();
//		return (Long) request.getAttribute(USER_ROLE_ID);
		AdminInfoVo adminInfo = getAdminInfo();
		return adminInfo.getRoleId();
	}
	
	/**
	 * @Description: 获取物业端登录用户有权限的社区IDList
	 * @Param: []
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/7/20
	 **/
	public static List<String> getAdminCommunityIdList() {
//		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
//			.getRequest();
//		return (List<String>) request.getAttribute(USER_COMMUNITY_ID_LIST);
		AdminInfoVo adminInfo = getAdminInfo();
		return adminInfo.getCommunityIdList();
	}
	
	/**
	* @Description: 校验当前登录用户小区权限
	 * @Param: [communityIdStr]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/7/20
	**/
	public static void validateCommunityId(Long communityId){
		List<String> communityIdList = getAdminCommunityIdList();
		if(!communityIdList.contains(String.valueOf(communityId))){
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
	public static void validateCommunityIds(Collection<String> communityIds){
		List<String> communityIdList = getAdminCommunityIdList();
		for(String communityId : communityIds){
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
		/*UUID.randomUUID().toString().replace("-", "");*/
		return String.valueOf(SnowFlake.nextId());//雪花算法 唯一键
	}
	
	/**
	* @Description: 设置token
	 * @Param: [typeName, o]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2020/12/4
	**/
	public static String setRedisToken(String typeName,Object o){
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
	public static void updateRedisByToken(String typeName, Object o, String token, Long expireTime) {
		redisTemplate.opsForValue().set(typeName + ":" + token, o, expireTime ,TimeUnit.HOURS);
	}

	/**
	* @Description: 设置token(带过期时间)
	 * @Param: [typeName, o, time, timeUnit]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2020/12/4
	**/
	public static String setRedisTokenWithTime(String typeName,Object o,long time,TimeUnit timeUnit){
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
	public static Object getRedisToken(String typeName,String token){
		return redisTemplate.opsForValue().get(typeName + ":" + token);
	}
	
	/**
	* @Description: 销毁token
	 * @Param: [typeName, token]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/4
	**/
	public static boolean destroyToken(String typeName,String token){
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
