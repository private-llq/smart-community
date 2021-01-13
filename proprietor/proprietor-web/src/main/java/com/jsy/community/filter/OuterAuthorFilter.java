package com.jsy.community.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.exception.JSYError;
import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author chq459799974
 * @description 外部访问(非APP)过滤
 * @since 2021-01-11 09:21
 **/
@Slf4j
@Component
//@WebFilter(urlPatterns = {"/api/v1/proprietor/out/pension/*"}, filterName = "pensionAuthorFilter")
//TODO 所有请求都会进来 urlPatterns失效问题待解决
@WebFilter(urlPatterns = {"/bbbcdefg"}, filterName = "outerAuthorFilter")
public class OuterAuthorFilter implements Filter{
	
	//要校验IP的接口路径前缀
	private static final Set<String> NEED_IP_AUTH_PREFIX_PATHS = Collections.unmodifiableSet(new HashSet<>(
			Arrays.asList("/api/v1/proprietor/out")));
	
	//允许IP前缀
	private static final Set<String> ALLOWED_PREFIX_IP = Collections.unmodifiableSet(new HashSet<>(
			Arrays.asList("192.168.12")));
	
	//允许IP前缀
	private static final Set<String> ALLOWED_IP = Collections.unmodifiableSet(new HashSet<>(
		Arrays.asList("222.178.212.29")));
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String path = req.getRequestURI().substring(req.getContextPath().length()).replaceAll("[/]+$", "");
//		System.out.println(path);
		boolean needIp = false;
		for (String prefix : NEED_IP_AUTH_PREFIX_PATHS) {
			if(path.startsWith(prefix)){
				needIp = true;
				break;
			}
		}
		if(needIp) {
			String ip = getIpAddr(req);
			boolean flag = false;
			if(ALLOWED_IP.contains(ip)){
				flag = true;
			}else{
				for (String prefix : ALLOWED_PREFIX_IP) {
					if(ip.startsWith(prefix)){
						flag = true;
						break;
					}
				}
			}
//			if(!flag){
//				log.error("接口" + path + "有非法访问尝试，来自IP：" + ip);
//				response.setCharacterEncoding("UTF-8");
//				PrintWriter out = response.getWriter();
//				out.println(JSONObject.parseObject(JSON.toJSONString(CommonResult.error(JSYError.INTERNAL.getCode(), "别试了别试了，功能已经下线了"))));
//				return;
//			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}
	
	private static String getIpAddr(HttpServletRequest request) throws UnknownHostException {
		
		// 从Nginx中X-Real-IP获取真实ip
		String ipAddress = request.getHeader("X-Real-IP");
		if (ipAddress != null && ipAddress.length() > 0 && !"unknown".equalsIgnoreCase(ipAddress)) {
//			System.out.println("从X-Real-IP中获取到ip:" + ipAddress);
			return ipAddress;
		}
		
		// 从Nginx中x-forwarded-for获取真实ip
		ipAddress = request.getHeader("x-forwarded-for");
		
		if (ipAddress != null && ipAddress.length() > 0 && !"unknown".equalsIgnoreCase(ipAddress)) {
			// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
			int index = ipAddress.indexOf(",");
			if (index > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
//			System.out.println("从x-forwarded-for中获取到ip:" + ipAddress);
		}
		
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
//			System.out.println("从Proxy-Client-IP中获取到ip:" + ipAddress);
		}
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
//			System.out.println("从WL-Proxy-Client-IP中获取到ip:" + ipAddress);
		}
		
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			// 根据网卡取本机配置的IP
			ipAddress = request.getRemoteAddr();
			if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
				ipAddress = InetAddress.getLocalHost().getHostAddress();
			}
//			System.out.println("从request.getRemoteAddr()中获取到ip:" + ipAddress);
		}
		return ipAddress;
		
	}
}
