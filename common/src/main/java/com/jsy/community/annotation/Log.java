package com.jsy.community.annotation;

import com.jsy.community.constant.LogConst;

/**
 * @ClassName：Log
 * @Description：自定义日志记录
 * @author：lihao
 * @date：2021/1/22 11:48
 * @version：1.0
 */
public @interface Log {
	
	/**
	 * @return BusinessType
	 * @Author lihao
	 * @Description 操作类型
	 * @Date 2021/1/22 11:54 
	 * @Param [] 
	 **/
	LogConst businessType() default LogConst.OTHER;
	
	/**
	 * @return java.lang.String
	 * @Author lihao
	 * @Description 备注说明
	 * @Date 2021/1/22 11:49
	 * @Param []
	 **/
	String remark() default "";
	
	/**
	 * @return boolean
	 * @Author lihao
	 * @Description 是否保存请求参数
	 * @Date 2021/1/22 11:53
	 * @Param []
	 **/
	boolean isSaveRequestData() default false;
	
}
