package com.jsy.community.aspectj;

import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IOpLogService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.OpLogEntity;
import com.jsy.community.utils.HttpUtils;
import com.jsy.community.utils.UserUtils;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
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
 * @description 用户操作日志AOP
 * @since 2021/8/21  14:28
 **/
@Aspect
@Component
public class OpLogAop extends BaseAop {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IOpLogService opLogService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IProprietorService proprietorService;

	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
	private IBaseUserInfoRpcService baseUserInfoRpcService;
	
	//定义切点 @Pointcut
	//在注解的位置切入代码
	@Pointcut("@annotation( com.jsy.community.annotation.businessLog)")
	public void opLogPointCut() {
	}
	
	//切面 配置通知
	@AfterReturning("opLogPointCut()")
	public void saveOpLog(JoinPoint joinPoint) {
		System.out.println("---进入用户操作日志切面---");
		//保存日志
		OpLogEntity opLog = new OpLogEntity();
		
		//从切面织入点处通过反射机制获取织入点处的方法
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		//获取切入点所在的方法
		Method method = signature.getMethod();
		
		//获取用户id和社区id
		opLog.setUserId(UserUtils.getUserId());
		opLog.setCommunityId(UserUtils.getAdminCommunityId());
		opLog.setCreateBy(UserUtils.getUserId());
		
		//获取操作
		businessLog businessLog = method.getAnnotation(businessLog.class);
		if (businessLog != null) {
			String operation = businessLog.operation();
			String content = businessLog.content();
			opLog.setOperation(operation);//保存获取的操作
			UserDetail userDetail = baseUserInfoRpcService.getUserDetail(UserUtils.getUserId());
			if (userDetail != null) {
				opLog.setContent(userDetail.getNickName() + content);//保存获取的内容
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
		opLogService.saveOpLog(opLog);
	}
}
