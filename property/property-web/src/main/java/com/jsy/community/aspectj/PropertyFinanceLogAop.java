package com.jsy.community.aspectj;

import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IPropertyFinanceLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FinanceLogEntity;
import com.jsy.community.utils.HttpUtils;
import com.jsy.community.utils.UserUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author DKS
 * @description 物业收款操作日志AOP
 * @since 2021/8/23  15:41
 **/
@Aspect
@Component
public class PropertyFinanceLogAop extends BaseAop {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyFinanceLogService propertyFinanceLogService;
	
	//定义切点 @Pointcut
	//在注解的位置切入代码
	@Pointcut("@annotation( com.jsy.community.annotation.PropertyFinanceLog)")
	public void FinanceLogPointCut() {
	}
	
	//切面 配置通知
	@AfterReturning("FinanceLogPointCut()")
	public void saveOpLog(JoinPoint joinPoint) {
		System.out.println("---进入物业收款操作日志切面---");
		//保存日志
		FinanceLogEntity financeLog = new FinanceLogEntity();
		
		//从切面织入点处通过反射机制获取织入点处的方法
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		//获取切入点所在的方法
		Method method = signature.getMethod();
		
		//获取用户id和社区id
		financeLog.setUserId(UserUtils.getUserId());
		financeLog.setCommunityId(UserUtils.getAdminCommunityId());
		financeLog.setCreateBy(UserUtils.getUserId());
		
		//获取操作
		businessLog businessLog = method.getAnnotation(businessLog.class);
		if (businessLog != null) {
			String operation = businessLog.operation();
			financeLog.setOperation(operation);//保存获取的操作
		}
		
		//获取请求的类名
		String className = joinPoint.getTarget().getClass().getName();
		//获取请求的方法名
		String methodName = method.getName();
		financeLog.setMethod(className + "." + methodName);
		
		//请求的参数
		Object[] args = joinPoint.getArgs();
		//将参数所在的数组转换成json
		String params = JSON.toJSONString(args);
		financeLog.setParams(params);
		
		//获取用户ip地址
		HttpServletRequest request = HttpUtils.getHttpServletRequest();
		financeLog.setIp(getIpAddr(request));
		
		//设置状态
		financeLog.setStatus("成功");
		
		//调用service保存SysLog实体类到数据库
		propertyFinanceLogService.saveFinanceLog(financeLog);
	}
}
