package com.jsy.community.aspectj;

import com.jsy.community.annotation.IpLimit;
import com.jsy.community.exception.JSYException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;


/**
 * IpLimit注解的 aop 拦截器
 * 用于接口单IP限流
 *
 * @author YuLF
 * @since 2021/1/20 11:10
 */
@Slf4j
@Aspect
@Component
@Order()
@RequiredArgsConstructor
public class IpLimitAop extends BaseAop {


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Pointcut("@annotation(com.jsy.community.annotation.IpLimit)")
    public void pointcut() {
    }

    /**
     * 拦截请求做验证处理
     *
     * @author YuLF
     * @since 2021/1/20 13:55
     */
    @Around("pointcut()")
    public Object ipLimit(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = getHttpServletRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> targetClass = point.getTarget().getClass();
        Method method = getMethod(targetClass, signature);
        IpLimit ipLimit = method.getAnnotation(IpLimit.class);
        String ip = getIpAddr(request);
        int limitCount = ipLimit.count();
        int second = ipLimit.second();
        List<String> keys = Collections.singletonList("ipLimit:"+ipLimit.prefix() + "_" + ip + method.getName());
        RedisScript<Number> redisScript = new DefaultRedisScript<>(redisLuaScript(), Number.class);
        Object count = stringRedisTemplate.execute(redisScript, keys, String.valueOf(limitCount), String.valueOf(second));
        log.info("IP: {} 在[{}]秒内，第{} ，访问描述为 [{}] 的接口", ip, second, getAccessCount(count, limitCount), ipLimit.desc());
        if (count != null && Integer.parseInt(count.toString()) <= limitCount) {
            return point.proceed();
        } else {
            throw new JSYException("访问超出频率限制，请在" + second + "秒后重试!");
        }
    }

    private String getAccessCount(Object count, int specifiedCount) {
        if (count == null || Integer.parseInt(count.toString()) > specifiedCount) {
            return specifiedCount + "+n次, 请求已被拦截!";
        } else {
            return count + "次";
        }
    }



    /**
     * 如果取出的值次数 大于 接口设定的次数 则直接返回
     * 否则自增一次，
     * ==1则表示是该ip第一次访问，则设置该key 的过期时间
     *
     * @return lua脚本
     */
    private String redisLuaScript() {
        return "local c" +
                "\nc = redis.call('get',KEYS[1])" +
                "\nif c and tonumber(c) > tonumber(ARGV[1]) then" +
                "\nreturn c;" +
                "\nend" +
                "\nc = redis.call('incr',KEYS[1])" +
                "\nif tonumber(c) == 1 then" +
                "\nredis.call('expire',KEYS[1],tonumber(ARGV[2]))" +
                "\nend" +
                "\nreturn c;";
    }



}
