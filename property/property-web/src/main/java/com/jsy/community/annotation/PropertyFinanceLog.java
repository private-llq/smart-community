package com.jsy.community.annotation;

import java.lang.annotation.*;

/**
 * @author DKS
 * @description 收款管理操作日志
 * @since 2021/8/23  16:48
 **/
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented //生成文档
public @interface PropertyFinanceLog {
	String operation() default "";
}
