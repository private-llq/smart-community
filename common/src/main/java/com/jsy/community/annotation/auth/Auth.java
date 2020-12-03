package com.jsy.community.annotation.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* @Description: 需要授权的操作
 * @Author: chq459799974
 * @Date: 2020/12/3
**/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
	boolean allowAnonymous() default false;
}
