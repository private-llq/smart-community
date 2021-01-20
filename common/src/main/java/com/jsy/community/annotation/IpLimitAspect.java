package com.jsy.community.annotation;

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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


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
public class IpLimitAspect {


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
        Method method = getDeclaredMethod(targetClass, signature.getName(),
                signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new JSYException("无法解析目标方法: " + signature.getMethod().getName());
        }
        IpLimit ipLimit = method.getAnnotation(IpLimit.class);
        String ip = getIpAddr(request);
        int limitCount = ipLimit.count();
        int second = ipLimit.second();
        List<String> keys = Collections.singletonList(ipLimit.prefix() + "_" + ip + method.getName());
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
     * 拿到request
     */
    private HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
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


    /**
     * 获取 IP地址
     * 1.使用了反向代理软件， 不能通过 request.getRemoteAddr()获取 IP地址
     * 2.如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，
     * 3.X-Forwarded-For中第一个非 unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        final String unknown = "unknown";
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 反射拿到类的方法
     *
     * @param clazz          类
     * @param name           类名
     * @param parameterTypes 方法的参数类型
     * @return 返回这个类的方法
     */
    private Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethod(superClass, name, parameterTypes);
            }
        }
        return null;
    }
}
