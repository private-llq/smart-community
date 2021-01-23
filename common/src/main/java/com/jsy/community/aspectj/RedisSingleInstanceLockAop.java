package com.jsy.community.aspectj;

import com.jsy.community.annotation.RedisSingleInstanceLock;
import com.jsy.community.utils.SpringContextUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面
 * @author zh_o
 * update for YuLF  2021-1-22 9:29
 * 注：此锁能应用在分布式模块服务下面，
 * 但是经测试：无法应用在redis 类似于主从架构下面，
 * 在把锁key存入redis时 redis的master此时如果挂了或者因为网络原因导致代码层面无法连接，在重新选举出的redis-master并没有这把锁存在，
 * 所以会造成资源共享的问题
 * 如果业务需要保证即使在 分布式微服务模块下 、 redis 多模块下主从同步架构，下这把锁还能在同一时间、同一资源、同一线程只有一个执行
 * 可以使用RedLock或者是Zookeeper实现的分布式锁，但是按照RedLock实现的锁在这种架构下面会极大的降低性能，而且redis集群之间还必须不能有协调数据
 */
@Slf4j
@Aspect
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisSingleInstanceLockAop extends BaseAop {

    private RedissonClient redissonClient = null;

    /**
     * 环绕通知
     * @param point 切入点
     */
    @Around("@annotation(com.jsy.community.annotation.RedisSingleInstanceLock)")
    public Object singleInstanceLockAround(ProceedingJoinPoint point) throws Throwable {
        //拿到方法签名
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        // 获取到方法对象
        Method method = getMethod(point.getTarget().getClass(), methodSignature);
        RedisSingleInstanceLock annotation = method.getAnnotation(RedisSingleInstanceLock.class);
        String key = annotation.lockKey();
        long lockTime = annotation.lockTime();
        long timout = annotation.waitTimout();
        // 定义锁
        RLock lock = null;
        // 定义返回值
        Object returnVal;
        try {
            redissonClient = getRedissonClient();
            // 获取锁
            lock = this.redissonClient.getLock("SingleInstanceLock:"+key);
            // 加锁 最多等待timout秒 lockTime秒后自动解锁
            lock.tryLock(timout, lockTime, TimeUnit.SECONDS);
            // 执行业务方法
            returnVal = point.proceed();
        } finally {
            if(lock != null){
                if(lock.isLocked()){
                    if(lock.isHeldByCurrentThread()){
                        lock.unlock();
                    }
                }
            }
        }
        return returnVal;
    }

    private RedissonClient getRedissonClient(){
        if( redissonClient == null ){
            synchronized (RedisSingleInstanceLockAop.class){
                if( redissonClient == null ){
                    redissonClient = (RedissonClient) SpringContextUtils.getBean("redissonClient");
                }
            }
        }
        return this.redissonClient;
    }
}
