package com.jsy.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁注解
 * @author zh_o
 * update for YuLF  2021-1-22 9:29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 存入Redis的Key
     */
    String lockKey();

    /**
     * 超时时间 是必填的，秒
     */
    long waitTimout();

    /**
     * 秒
     * 上锁时间
     */
    long lockTime() default 30;


}
