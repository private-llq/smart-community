package com.jsy.community.intercepter;


import com.jsy.community.annotation.Perms;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
* @Description: 大后台登录和权限拦截
 * @Author: chq459799974
 * @Date: 2020/12/14
**/
@Component
//public class AdminInterceptor extends HandlerInterceptorAdapter {
//public class AdminInterceptor extends OncePerRequestFilter {
public class AdminInterceptor {
	public static final String USER_KEY = "userId";
	
	@Autowired
	private RedisUtils redisUtils;
	
//	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		String token = request.getHeader("token");
		
		//===================================== 登录验证(大后台所有操作都需要登录) ========================================
		Long uid = redisUtils.getUid(token);
		if(uid == null){
			throw new JSYException(JSYError.UNAUTHORIZED.getCode(), "登录过期");
		}else{
			request.setAttribute(USER_KEY, uid);
		}
		
		//===================================== 权限验证 ========================================
		Perms methodAnnotation, classAnnotation;
		if (handler instanceof HandlerMethod) {
			methodAnnotation = ((HandlerMethod) handler).getMethodAnnotation(Perms.class);
//			classAnnotation = ((HandlerMethod) handler).getBeanType().getAnnotation(Perms.class);
		} else{
			return true;
		}
		
		//Perms需要验证
		List<Integer> roleIdList = redisUtils.getRoleIdList(token);
		if(CollectionUtils.isEmpty(roleIdList)){
			throw new JSYException(JSYError.UNAUTHORIZED.getCode(), "无操作权限");
		}
		
		
		return true;
	}
	
}
