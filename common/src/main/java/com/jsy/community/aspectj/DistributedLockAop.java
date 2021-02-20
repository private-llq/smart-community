package com.jsy.community.aspectj;

import com.jsy.community.annotation.DistributedLock;
import com.jsy.community.exception.JSYException;
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
 */
@Slf4j
@Aspect
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DistributedLockAop extends BaseAop {



    private RedissonClient redissonClient = null;

    /**
     * 环绕通知
     * @param point 切入点
     */
    @Around("@annotation(com.jsy.community.annotation.DistributedLock)")
    public Object singleInstanceLockAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = getMethod(point.getTarget().getClass(), methodSignature);
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);
        String key = annotation.lockKey();
        long lockTime = annotation.lockTime();
        long timout = annotation.waitTimout();
        // 定义锁
        RLock lock = null;
        try {
            redissonClient = getRedissonClient();
            // 获取锁
            lock = this.redissonClient.getLock("SingleInstanceLock:"+key);
            // 加锁 最多等待timout秒 lockTime秒后自动解锁
            boolean lockSuccess = lock.tryLock(timout, lockTime, TimeUnit.SECONDS);
            if(lockSuccess){
                // 执行业务方法
                return point.proceed();
            }
        } catch (Exception e){
            log.error(this.getClass().getPackageName()+".singleInstanceLockAround：{}", e.getCause() );
            throw new JSYException("方法执行失败, 请重试!");
        } finally {
            if(lock != null){
                if(lock.isLocked()){
                    if(lock.isHeldByCurrentThread()){
                        lock.unlock();
                    }
                }
            }
        }
         //失败最典型的原因是redis挂了
         throw new JSYException("加锁失败, 本次操作取消, 请重试! ");
    }

    private RedissonClient getRedissonClient(){
        if( redissonClient == null ){
            synchronized (DistributedLockAop.class){
                if( redissonClient == null ){
                    redissonClient = (RedissonClient) SpringContextUtils.getBean("redissonClient");
                }
            }
        }
        return this.redissonClient;
    }
}
