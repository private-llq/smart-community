package com.jsy.community.annotation;

import com.jsy.community.constant.LogModule;
import com.jsy.community.constant.LogTypeConst;

import java.lang.annotation.*;

/**
 * @ClassName：Log
 * @Description：业主重要操作日志记录
 * @author：lihao
 * @date：2021/1/22 11:48
 * @version：1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

	/**
	 * 操作类型
	 */
	String operationType() default LogTypeConst.OTHER;

	/**
	 * 操作模块
	 */
	String module() default LogModule.OTHER;

	/**
	 * 功能描述
	 */
	String explain() default "";

	/**
	 * 是否保存请求参数
	 */
	boolean isSaveRequestData() default true;

}
