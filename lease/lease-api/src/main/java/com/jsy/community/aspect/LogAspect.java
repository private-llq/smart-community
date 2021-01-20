package com.jsy.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * @author lihao
 * @ClassName LogAcpect
 * @Date 2021/1/19  15:28
 * @Description 日志切面类
 * @Version 1.0
 **/
@Aspect
@Component
public class LogAspect {
	private Logger logger = LoggerFactory.getLogger(LogAspect.class);
	
	/**
	 * 定义切入点，切入点为com.jsy.community下的函数
	 */
	// com.jsy.community.intercepter  会提示没有intercepter的包  不用管 正常的
	@Pointcut("execution( * com.jsy.community..*.*(..)) && !execution(* com.jsy.community.intercepter..*.*(..)) && !execution(* com.jsy.community.exception..*.*(..))")
	public void webLog() {
	}
	
	/**
	 * 前置通知：在连接点之前执行的通知
	 *
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Before("webLog()")
	public void doBefore(JoinPoint joinPoint) throws Throwable {
		// 接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			// 记录下请求内容    --测试  后面日志输出级别改成   debug
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
			Calendar ca = Calendar.getInstance();
			String time = df.format(ca.getTime());
			logger.info("");
			logger.info("访问时间 : " + time);
			logger.info("访问路径 : " + request.getRequestURL().toString());
			logger.info("请求方式 : " + request.getMethod());
			logger.info("访问方法 : " + joinPoint.getSignature().getName());
			logger.info("访问IP : " + request.getRemoteAddr());
			logger.info("方法参数 : " + Arrays.toString(joinPoint.getArgs()));
		}
	}
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 后置通知
	 * @Date 2021/1/19 17:09
	 * @Param [ret]
	 **/
	@AfterReturning(returning = "ret", pointcut = "webLog()")
	public void doAfterReturning(Object ret) throws Throwable {
		// 处理完请求，返回内容
		logger.info("返回结果 : " + ret);
	}
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 异常通知
	 * @Date 2021/1/19 17:43
	 * @Param [joinPoint, ex]
	 **/
	@AfterThrowing(pointcut = "webLog()", throwing = "ex")
	public void afterThrowing(JoinPoint joinPoint, Exception ex) {
		String methodName = joinPoint.getSignature().getName();
		logger.info("异常信息 : " + methodName + "() 出现了异常——————" + ex.getMessage());
	}
}
