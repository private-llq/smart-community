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
public @interface RedisSingleInstanceLock {

    /**
     * 存入Redis的Key
     */
    String lockKey();

    /**
     * 超时时间 是必填的，
     * 代表在并发的时候，第二个线程最多等待第一个线程持有锁的时间，
     * 等待超时时间 不要低于业务的执行时间，否则会造成多个线程在同一时间争抢资源
     */
    long waitTimout();

    /**
     * 上锁时间
     */
    long lockTime() default 30;


}
