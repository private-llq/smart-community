package com.jsy.community.aspectj;

import com.jsy.community.annotation.RequireRecentBrowse;
import com.jsy.community.api.IHouseRecentService;
import com.jsy.community.constant.Const;
import com.jsy.community.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author YuLF
 * @since 2021-02-18 11:37
 */
@Slf4j
@Aspect
@Component
public class RequireRecentBrowseAop extends BaseAop {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseRecentService houseRecentService;

    @Pointcut("@annotation(com.jsy.community.annotation.RequireRecentBrowse)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object desensitization(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Object proceed = point.proceed();
        Class<?> targetClass = point.getTarget().getClass();
        Method method = getMethod(targetClass, signature);
        RequireRecentBrowse annotation = method.getAnnotation(RequireRecentBrowse.class);
        String isMark = annotation.notMarkRequestParameter();
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        String parameter = httpServletRequest.getParameter(isMark);
        //如果携带了不标记最近浏览数据的请求参数 则直接返回
        if (parameter != null) {
            return proceed;
        }
        //保存用户浏览数据
        houseRecentService.saveLeaseBrowse(proceed, UserUtils.getUserId());
        return proceed;
    }


}



