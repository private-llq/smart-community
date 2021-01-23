package com.jsy.community.aspectj;

import com.jsy.community.exception.JSYException;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author YuLF
 * @since 2021-01-23 11:14
 */
public class BaseAop {

    public HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    public Method getMethod(Class<?> clazz, MethodSignature signature){
        Method method = getDeclaredMethod(clazz, signature.getName(),
                signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new JSYException("无法解析目标方法: " + signature.getMethod().getName());
        }
        return method;
    }

    public Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
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

    /**
     * 获取 IP地址
     * 1.使用了反向代理软件， 不能通过 request.getRemoteAddr()获取 IP地址
     * 2.如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，
     * 3.X-Forwarded-For中第一个非 unknown的有效IP字符串，则为真实IP地址
     */
    public String getIpAddr(HttpServletRequest request) {
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

}
