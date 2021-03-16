package com.jsy.community.aspectj;

import cn.hutool.core.util.StrUtil;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.UserInfoVo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * @author lihao
 * @ClassName LogAcpect
 * @Date 2021/1/19  15:28
 * @Description 日志控制台切面类
 * @Version 1.0
 **/
@Aspect
@Component
@Scope(value = "prototype")
public class ConsoleLogAspect {
	private final Logger logger = LoggerFactory.getLogger(ConsoleLogAspect.class);
	
	@Autowired
	private UserUtils userUtils;
	
	private static final ThreadLocal<Double> mark = new ThreadLocal<>();
	
	/**
	 * 定义切入点，切入点为com.jsy.community下的函数
	 */
	// com.jsy.community.intercepter  会提示没有intercepter的包  不用管 正常的
//	@Pointcut("execution( * com.jsy.community..*.*(..)) && !execution(* com.jsy.community.intercepter..*.*(..)) && !execution(* com.jsy.community.exception..*.*(..))")
	
	// 只拦截controller的方法
	@Pointcut("execution(* com.jsy.community..controller.*.*(..))")
	public void webLog() {
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
			logger.info("");
			logger.info("");
			logger.info("");
			logger.info("访问时间 : " + time);
			logger.info("访问路径 : " + request.getRequestURL().toString()+"    "+"请求方式 : " + request.getMethod());
//			logger.info("访问方法 : " + joinPoint.getSignature().getName());
			logger.info("方法参数 : " + Arrays.toString(joinPoint.getArgs()));
			logger.info("访问IP : " + request.getRemoteAddr());
			
			//2. 1判断方法上是否有@Login注解
			Login method_login = getAnnotationLoginWithMethod(joinPoint);
			Login class_login = getAnnotationLoginWithClass(joinPoint);
			//2. 2判断类上是否有@Login注解      反正只要有助解【不管他的值是true还是false】 就去获取  然后判断是否有  请求头为token
			if (method_login != null || class_login != null) {
				if (attributes != null) {
					HttpServletRequest servletRequest = attributes.getRequest();
					String token = servletRequest.getHeader("token");
					if (!StrUtil.isBlank(token)) {
						UserInfoVo userInfo = userUtils.getUserInfo(token);
						if (userInfo != null) {
							logger.info("访问用户："+userInfo.getRealName()+"     用户id："+userInfo.getUid());
						}
					}
				}
			}
			double start = System.currentTimeMillis();
			mark.set(start);
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
//		logger.info("返回结果 : " + ret);
		double end = System.currentTimeMillis();
		Double start = mark.get();
		logger.info("总耗时 : " + (end-start)+"ms");
	}
}
