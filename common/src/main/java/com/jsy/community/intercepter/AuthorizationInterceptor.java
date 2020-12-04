package com.jsy.community.intercepter;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.auth.Auth;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * 权限(Token)验证
 */
@Component
@ConditionalOnProperty(value = "jsy.web.enable", havingValue = "true")
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
	public static final String USER_KEY = "userId";
	public static final String USER_INFO = "userInfo";
	
	@Autowired
	private UserUtils userUtils;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		//===================================== 需要授权的敏感操作 ========================================
		Auth authAnnotation;
		if (handler instanceof HandlerMethod) {
			authAnnotation = ((HandlerMethod) handler).getMethodAnnotation(Auth.class);
			if(authAnnotation != null){
				String token = request.getHeader("token");
				if (StrUtil.isBlank(token)) {
					token = request.getParameter("token");
				}
				Object authTokenContent = userUtils.getRedisToken("Auth", token);
				if(authTokenContent == null){
					throw new JSYException(JSYError.UNAUTHORIZED.getCode(), "操作未被授权");
				}
				String body = readBody(request);
				JSONObject jsonObject = JSONObject.parseObject(body);
				if(jsonObject == null || !(String.valueOf(authTokenContent).equals(jsonObject.getString("account")))){
					throw new JSYException(JSYError.UNAUTHORIZED.getCode(), "操作未被授权");
				}
				request.setAttribute("body", body);
				return true;
			}
		}
		
		//===================================== 登录验证 ========================================
		Login methodAnnotation, classAnnotation;
		if (handler instanceof HandlerMethod) {
			methodAnnotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
			classAnnotation = ((HandlerMethod) handler).getBeanType().getAnnotation(Login.class);
		} else {
			return true;
		}
		
		if (methodAnnotation == null && classAnnotation == null) {
			// 都没有
			return true;
		}
		
		if (methodAnnotation != null && methodAnnotation.allowAnonymous()) {
			// 方法注解允许匿名访问
			return true;
		}
		
		if (methodAnnotation == null && classAnnotation.allowAnonymous()) {
			// 注解在类中，并且允许匿名访问
			return true;
		}
		
		String token = request.getHeader("token");
		if (StrUtil.isBlank(token)) {
			token = request.getParameter("token");
		}
		UserInfoVo userInfo = userUtils.getUserInfo(token);
		if(userInfo == null){
			throw new JSYException(JSYError.UNAUTHORIZED.getCode(), "登录过期");
		}
		request.setAttribute(USER_INFO, userInfo);
		request.setAttribute(USER_KEY, userInfo.getUid());
		
		return true;
	}
	
	private String readBody(HttpServletRequest request){
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			System.out.println("读请求体异常");
			throw new RuntimeException(e);
		}
		return sb.toString();
	}
	
}
