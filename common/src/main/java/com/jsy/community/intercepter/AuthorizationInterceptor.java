package com.jsy.community.intercepter;


import cn.hutool.core.util.StrUtil;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限(Token)验证
 */
@Component
@ConditionalOnProperty(value = "jsy.web.enable", havingValue = "true")
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
	public static final String USER_KEY = "userId";
	@Resource
	private JwtUtils jwtUtils;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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
		
		//获取用户凭证
		String token = request.getHeader(jwtUtils.getHeader());
		if (StrUtil.isBlank(token)) {
			token = request.getParameter(jwtUtils.getHeader());
		}
		
		//凭证为空
		if (StrUtil.isBlank(token)) {
			throw new JSYException(JSYError.UNAUTHORIZED.getCode(), jwtUtils.getHeader() + "不能为空");
		}
		
		Claims claims = jwtUtils.getClaimByToken(token);
		if (claims == null || jwtUtils.isTokenExpired(claims.getExpiration())) {
			throw new JSYException(JSYError.UNAUTHORIZED.getCode(), jwtUtils.getHeader() + "失效，请重新登录");
		}
		
		//设置userId到request里，后续根据userId，获取用户信息
		request.setAttribute(USER_KEY, Long.parseLong(claims.getSubject()));
		
		return true;
	}
}
