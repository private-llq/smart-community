package com.jsy.community.aspectj;

import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.entity.SysOpLogEntity;
import com.jsy.community.service.ISysOpLogService;
import com.jsy.community.service.ISysUserService;
import com.jsy.community.utils.HttpUtils;
import com.jsy.community.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author DKS
 * @description 用户操作日志AOP
 * @since 2021/10/20  10:41
 **/
@Aspect
@Component
public class SysOpLogAop extends BaseAop {
	
	@Resource
	private ISysOpLogService sysOpLogService;
	
	@Resource
	private ISysUserService sysUserService;
	
	//定义切点 @Pointcut
	//在注解的位置切入代码
	@Pointcut("@annotation( com.jsy.community.annotation.businessLog)")
	public void opLogPointCut() {
	}
	
	//切面 配置通知
	@AfterReturning("opLogPointCut()")
	public void saveOpLog(JoinPoint joinPoint) {
		System.out.println("---进入大后台用户操作日志切面---");
		//保存日志
		SysOpLogEntity opLog = new SysOpLogEntity();
		
		//从切面织入点处通过反射机制获取织入点处的方法
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		//获取切入点所在的方法
		Method method = signature.getMethod();
		
		//获取用户id和社区id
		opLog.setUserId(UserUtils.getId());
		opLog.setCreateBy(UserUtils.getId());
		
		//获取操作
		businessLog businessLog = method.getAnnotation(businessLog.class);
		if (businessLog != null) {
			String operation = businessLog.operation();
			String content = businessLog.content();
			opLog.setOperation(operation);//保存获取的操作
			String sysRealName = sysUserService.getSysRealName(UserUtils.getId());
			if (StringUtils.isNotBlank(sysRealName)) {
				opLog.setContent(sysRealName + content);//保存获取的内容
			}
		}
		
		//获取请求的类名
		String className = joinPoint.getTarget().getClass().getName();
		//获取请求的方法名
		String methodName = method.getName();
		opLog.setMethod(className + "." + methodName);
		
		//请求的参数
		Object[] args = joinPoint.getArgs();
		//将参数所在的数组转换成json
		String params = JSON.toJSONString(args);
		opLog.setParams(params);
		
		//获取用户ip地址
		HttpServletRequest request = HttpUtils.getHttpServletRequest();
		opLog.setIp(getIpAddr(request));
		
		//调用service保存SysLog实体类到数据库
		sysOpLogService.saveOpLog(opLog);
	}
}
