package com.jsy.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 最近的租赁浏览 添加数据增加至数据库注解
 * 此注解标明了进入租赁房屋详情时 标记该条数据为用户最近浏览数据
 * @author YuLF
 * @since 2021-02-18 11:28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRecentBrowse {
    /**
     * 请求参数上面有此Url 表示 这个租赁浏览不标记为用户的最近浏览
     * 除此之外没有携带此参数的 都表示需要标记为用户的最近浏览
     */
    String notMarkRequestParameter() default "notMarkBrowse";
}
