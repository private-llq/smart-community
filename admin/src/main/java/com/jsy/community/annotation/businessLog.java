package com.jsy.community.annotation;

import java.lang.annotation.*;

/**
 * @author DKS
 * @description 用户操作日志注解
 * @since 2021/8/21  15:04
 **/
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented //生成文档
public @interface businessLog {
	String operation() default "";
	
	String content() default "";
	
}
