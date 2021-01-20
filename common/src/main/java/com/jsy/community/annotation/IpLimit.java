package com.jsy.community.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ip访问限制注解
 * @author YuLF
 * @since  2021/1/20 11:20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IpLimit {

    /**
     * 用于描述接口功能
     */
    String desc() default "";


    /**
     * 存入redis的prefix 这个可以随意，但是最好是和功能相关的
     */
    String prefix() default "";

    /**
     * 时间范围，单位秒
     */
    int second();

    /**
     * 限制访问次数
     */
    int count();

}
