package com.jsy.community.aspectj;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.Log;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.log.ProprietorLog;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.UserInfoVo;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author lihao
 * @ClassName LogAspect
 * @Date 2021/1/22  13:19
 * @Description 业主日志切面类
 * @Version 1.0
 **/
@Aspect
@Component
public class ProprietorLogAspect {
	
	@Autowired
	private UserUtils userUtils;
	
	private static final Logger log = LoggerFactory.getLogger(ProprietorLogAspect.class);
	
	/**
	 * 切入点方法执行成功
	 */
	private static final int SUCCESS = 0;
	
	/**
	 * 切入点方法执行失败
	 */
	private static final int FAIL = 1;
	
	/**
	 * 远程调用保存日志地址接口
	 */
	private static final String SAVELOGADDRESSRPC = "http://localhost:7001/api/v1/property/community/proprietorLog/saveProprietorLog";
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 配置切入点
	 * @Date 2021/1/22 15:48
	 * @Param []
	 **/
	@Pointcut("@annotation(com.jsy.community.annotation.Log)")
	public void logPointCut() {
	}
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 前置通知
	 * @Date 2021/1/22 17:03
	 * @Param []
	 **/
	@Before("logPointCut()")
	public void doBefore() {
	}
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 环绕通知
	 * @Date 2021/1/22 16:26
	 * @Param [joinPoint, res]
	 **/
	@Around("logPointCut()")
	public Object doAround(ProceedingJoinPoint point) throws Throwable {
		// 获取开始时间
		Long startTime = System.currentTimeMillis();
		// 执行目标方法
		Object proceed = point.proceed();
		// 获取结束时间
		long endTime = System.currentTimeMillis();
		handleLog(point, null, startTime, endTime);
		return proceed;
	}
	
	
	@AfterThrowing(pointcut = "logPointCut()", throwing = "ex")
	public void doAfterThrowing(JoinPoint joinPoint, Exception ex) {
		// 获取开始时间
		Long startTime = System.currentTimeMillis();
		// 获取结束时间
		long endTime = System.currentTimeMillis();
		handleLog(joinPoint, ex, startTime, endTime);
	}
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 处理日志
	 * @Date 2021/1/22 16:27
	 * @Param [joinPoint, e, jsonResult]
	 **/
	public void handleLog(final JoinPoint joinPoint, final Exception e, final Long startTime, final Long endTime) {
		ProprietorLog proprietorLog = new ProprietorLog();
		try {
			// 获取执行时间
			long sumTime = endTime - startTime;
			proprietorLog.setRuntime(sumTime);
			
			//1. 获取访问路径，访问ip，请求方式
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attributes != null) {
				HttpServletRequest request = attributes.getRequest();
				String url = request.getRequestURL().toString();
				String requestMethod = request.getMethod();
				String ipAddress = request.getRemoteAddr();
				proprietorLog.setUrl(url);
				proprietorLog.setRequestMethod(requestMethod);
				proprietorLog.setIpAddress(ipAddress);
			}
			
			//2. 获取操作人信息[来源:注解@Login]
			//2. 1判断方法上是否有@Login注解
			Login method_login = getAnnotationLoginWithMethod(joinPoint);
			Login class_login = getAnnotationLoginWithClass(joinPoint);
			//2. 2判断类上是否有@Login注解      反正只要有助解【不管他的值是true还是false】 就去获取  然后判断是否有  请求头为token
			if (method_login != null || class_login != null) {
				if (attributes != null) {
					HttpServletRequest request = attributes.getRequest();
					String token = request.getHeader("token");
					if (!StrUtil.isBlank(token)) {
						UserInfoVo userInfo = userUtils.getUserInfo(token);
						if (userInfo != null) {
							proprietorLog.setName(userInfo.getRealName());
							proprietorLog.setCity(userInfo.getCity());
							proprietorLog.setDetailAddress(userInfo.getDetailAddress());
//                          具体要保存用户哪些信息  还不确定需求
//						    proprietorLog.setPhone()
//						    proprietorLog.setCommunity(userInfo.)
//							..
						}
					}
				}
			}
			
			//3. 获取操作类型 操作模块 功能描述 是否保存请求参数 [来源注解]
			Log annotationLog = getAnnotationLog(joinPoint);
			if (annotationLog != null) {
				String explain = annotationLog.explain();
				String module = annotationLog.module();
				String operationType = annotationLog.operationType();
				proprietorLog.setIntroduce(explain);
				proprietorLog.setModule(module);
				proprietorLog.setOperationType(operationType);
				// 是否保存请求参数
				boolean flag = annotationLog.isSaveRequestData();
				if (flag) {
					
					//4. 获取请求参数
					Object[] args = joinPoint.getArgs();
					String parameter = args.toString();
					proprietorLog.setParameter(parameter);
				}
				
				//5. 获取请求结果[能走到后置通知就说明没有报错]
				proprietorLog.setStatus(SUCCESS);
				System.out.println(proprietorLog);
				
				//6. 是否有异常
				if (e != null) {
					proprietorLog.setStatus(FAIL);
					proprietorLog.setExceptionInfo(e.getMessage());
				}
				
				//7. 保存数据  【目前保存日志是通过物业端来保存的   具体上线的时候保存在哪  到时候再看】
				saveLog(proprietorLog);
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 方法上是否存在Log注解，如果存在就获取
	 */
	private Log getAnnotationLog(JoinPoint joinPoint) throws Exception {
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		
		if (method != null) {
			return method.getAnnotation(Log.class);
		}
		return null;
	}
	
	
	/**
	 * 判断使用log注解的方法所在类上是否存在Log注解，如果存在就获取
	 */
	private Login getAnnotationLoginWithClass(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		Class declaringType = signature.getDeclaringType();
		if (declaringType != null) {
			return (Login) declaringType.getAnnotation(Login.class);
		}
		return null;
	}
	
	/**
	 * 判断使用log注解的方法上是否存在Login注解，如果存在就获取
	 */
	private Login getAnnotationLoginWithMethod(JoinPoint joinPoint) throws Exception {
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		
		if (method != null) {
			return method.getAnnotation(Login.class);
		}
		return null;
	}
	
	/**
	 * 将日志数据保存到数据库
	 */
	private void saveLog(ProprietorLog proprietorLog) {
		// 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		// 创建Post请求
		HttpPost httpPost = new HttpPost(SAVELOGADDRESSRPC);
		
		if (httpPost == null) {
			return;
		}
		
		// 我这里利用阿里的fastjson，将Object转换为json字符串;
		// (需要导入com.alibaba.fastjson.JSON包)
		String jsonString = JSON.toJSONString(proprietorLog);
		
		StringEntity entity = new StringEntity(jsonString, "UTF-8");
		
		// post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
		httpPost.setEntity(entity);
		
		httpPost.setHeader("Content-Type", "application/json;charset=utf8");
		
		// 响应模型
		CloseableHttpResponse response = null;
		try {
			// 由客户端执行(发送)Post请求
			response = httpClient.execute(httpPost);
			// 从响应模型中获取响应实体
			HttpEntity responseEntity = response.getEntity();
			
			System.out.println("响应状态为:" + response.getStatusLine());
			if (responseEntity != null) {
				System.out.println("响应内容长度为:" + responseEntity.getContentLength());
				System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
//			log.info("异常开始发生了");
//			log.error("异常："+e.getMessage());
//			log.info("异常产生原因：物业端没有打开  记录日志 目前采用的是记录到物业端  若没有打开物业端不会影响正常业务 只是不保存业主操作记录 ");
//			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
