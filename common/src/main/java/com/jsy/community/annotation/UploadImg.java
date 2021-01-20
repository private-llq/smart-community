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
	String bucketName();
	String redisKeyName();
}
