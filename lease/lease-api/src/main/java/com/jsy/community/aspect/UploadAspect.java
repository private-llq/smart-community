package com.jsy.community.aspect;

import com.jsy.community.annotation.UploadImg;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.vo.CommonResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;

/**
 * @author lihao
 * @ClassName UploadAspect
 * @Date 2021/1/20  11:37
 * @Description 文件上传切面类
 * @Version 1.0
 **/

@Component
@Aspect
public class UploadAspect {
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 切入点表达式
	 * @Date 2021/1/20 14:08
	 * @Param []
	 **/
	@Pointcut("@annotation(com.jsy.community.annotation.UploadImg)")
	public void imgPoint() {
	}
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 前置通知
	 * @Date 2021/1/20 14:08
	 * @Param [joinPoint]
	 **/
	@Before("imgPoint()")
	public void doBefore(JoinPoint joinPoint) throws Throwable {
		//1. 获取注解上的bucketName 和 redisKeyName
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		
		Method method = signature.getMethod();
		System.out.println(method.getName());
		Class<?>[] parameterTypes = method.getParameterTypes();
		
		Class returnType = signature.getReturnType();
		System.out.println(returnType);
		System.out.println(method.getParameterTypes());
		
		String declaringTypeName = signature.getDeclaringTypeName();
		Class<?> cls = Class.forName(declaringTypeName);
		
		Method declaredMethod = cls.getDeclaredMethod(signature.getName(), signature.getParameterTypes());
		UploadImg annotation = declaredMethod.getAnnotation(UploadImg.class);
		String bucketName = annotation.bucketName();
		String redisKeyName = annotation.redisKeyName();
		
		//2. 对上传的文件进行处理
		Object[] args = joinPoint.getArgs();
		MultipartFile[] files = (MultipartFile[]) args[0];
		if (files != null && files.length > 0) {
			String[] fileList = MinioUtils.uploadForBatch(files, bucketName);
			
			// 文件上传之后，需要把文件名存到redis里面
			for (String s : fileList) {
				redisTemplate.opsForSet().add(redisKeyName, s);
			}
			
			CommonResult result = (CommonResult) args[1];
			if (result != null) {
				result.setData(fileList);
			}
		}
	}
}
