package com.jsy.community.aspectj;

import com.jsy.community.annotation.UploadImg;
import com.jsy.community.exception.JSYException;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

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
					
					try {
						CommonResult result = (CommonResult) args[1];
						if (result != null) {
							result.setData(fileList);
						}
					} catch (Exception e) {
						throw new JSYException("第二个参数请设置为CommonResult");
					}
				}
				// 如果方法的该参数是 StandardMultipartFile  表示上传的是一张图片
				if ("StandardMultipartFile".equals(typeName)) {
					MultipartFile file = (MultipartFile) arg;
					String filePath = MinioUtils.upload(file, bucketName);
					
					// 文件上传之后，需要把文件名存到redis里面
					if (!StringUtils.isEmpty(filePath)) {
						redisTemplate.opsForSet().add(redisKeyName, filePath);
					}
					
					try {
						CommonResult result = (CommonResult) args[1];
						if (result != null) {
							result.setData(filePath);
						}
					} catch (Exception e) {
						throw new JSYException("第二个参数请设置为CommonResult");
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
			if (("class [Ljava.lang.String;").equals(declaredField.getGenericType().toString())) {
				// 拿到该属性的gettet方法
				Method m = arg.getClass().getMethod("get" + getMethodName(declaredField.getName()));
				String[] val = (String[]) m.invoke(arg);// 调用getter方法获取属性值
				if (val != null) {
					for (String s : val) {
						redisTemplate.opsForSet().add(redisKeyName, s);
					}
				}
			} else if (("class java.lang.String").equals(declaredField.getGenericType().toString())) {
				// 拿到该属性的gettet方法
				Method m = arg.getClass().getMethod("get" + getMethodName(declaredField.getName()));
				String val = (String) m.invoke(arg);// 调用getter方法获取属性值
				if (val != null) {
					redisTemplate.opsForSet().add(redisKeyName, val);
				}
			} else if ("java.util.List<java.lang.String>".equals(declaredField.getGenericType().toString())) {
				Method m = arg.getClass().getMethod("get" + getMethodName(declaredField.getName()));
				List<String> val = (List<String>) m.invoke(arg);// 调用getter方法获取属性值
				if (val != null) {
					for (String s : val) {
						redisTemplate.opsForSet().add(redisKeyName, s);
					}
				}
			}else {
				throw new JSYException("请指定您实体接收图片的属性或您指定的属性不符合要求，支持String，String[],List<String>");
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
