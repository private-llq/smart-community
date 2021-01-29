package com.jsy.community.annotation;

import java.lang.annotation.*;

/**
 * @ClassName：UploadImg
 * @Description：文件上传注解
 * @author：lihao
 * @date：2021/1/20 11:31
 * @version：1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UploadImg {
	/**
	 * @Author lihao
	 * @Description 存储到服务器的BUCKET名称
	 **/
	String bucketName() default "other";
	
	/**
	 * @Author lihao
	 * @Description 存到redis时的键名
	 **/
	String redisKeyName();
	
	/**
	 * @Author lihao
	 * @Description 实体的哪个属性名是保存的图片地址
	 **/
	String attributeName() default "without";
	
}
