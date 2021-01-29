package com.jsy.community.aspectj;

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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

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
		//1. 获取注解上的bucketName[保存图片时存放在服务器哪个BUCKET] , redisKeyName[存放图片名称的redis键] , onlyImage[是否只上传图片] , attributeName[实体的哪个属性名是保存的图片地址]
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String declaringTypeName = signature.getDeclaringTypeName();
		Class<?> cls = Class.forName(declaringTypeName);
		
		Method declaredMethod = cls.getDeclaredMethod(signature.getName(), signature.getParameterTypes());
		UploadImg annotation = declaredMethod.getAnnotation(UploadImg.class);
		String bucketName = annotation.bucketName();
		String redisKeyName = annotation.redisKeyName();
		String attributeName = annotation.attributeName();
		
		// 该功能只是上传图片
		if (attributeName.equals("without")) {
			//2. 对上传的文件进行处理
			Object[] args = joinPoint.getArgs();
			for (Object arg : args) {
				String typeName = arg.getClass().getSimpleName();
				// 如果方法的该参数是 MultipartFile[]
				if ("MultipartFile[]".equals(typeName)) {
					MultipartFile[] files = (MultipartFile[]) arg;
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
			return;
		}
		
		// 该功能是保存数据，数据中含有图片地址
		Object[] args = joinPoint.getArgs();
		for (Object arg : args) {
			Class<?> aClass = arg.getClass();
			Field declaredField = aClass.getDeclaredField(attributeName);
			declaredField.setAccessible(true);
			Type genericType = declaredField.getGenericType();
			String s1 = declaredField.getGenericType().toString();
			
			// 如果GenericType是数组类型
			if (declaredField.getGenericType().toString().equals("class [Ljava.lang.String;")) {
				// 拿到该属性的gettet方法
				Method m = (Method) arg.getClass().getMethod("get" + getMethodName(declaredField.getName()));
				String[] val = (String[]) m.invoke(arg);// 调用getter方法获取属性值
				if (val != null) {
					for (String s : val) {
						redisTemplate.opsForSet().add(redisKeyName, s);
					}
				}
			}
			
			// 如果GenericType是String类型
			if (declaredField.getGenericType().toString().equals("class java.lang.String")) {
				// 拿到该属性的gettet方法
				Method m = (Method) arg.getClass().getMethod("get" + getMethodName(declaredField.getName()));
				String val = (String) m.invoke(arg);// 调用getter方法获取属性值
				if (val != null) {
					redisTemplate.opsForSet().add(redisKeyName, val);
				}
			}
		}
	}
	
	// 把一个字符串的第一个字母大写、效率是最高的。
	private static String getMethodName(String fildeName) throws Exception {
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}
}
