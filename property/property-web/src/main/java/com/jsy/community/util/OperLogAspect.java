package com.jsy.community.util;


import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.AdminRoleService;
import com.jsy.community.api.AdminUserRoleService;
import com.jsy.community.api.CarOperationService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminUserRoleEntity;
import com.jsy.community.entity.property.CarOperationLog;
import com.jsy.community.utils.UserUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


/**
 * 切面处理类，操作日志异常日志记录处理
 *
 * @author arli
 * @date 2019/03/21
 */
@Aspect
@Component
public class OperLogAspect {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private CarOperationService carOperationService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private AdminUserRoleService adminUserRoleService;


    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.jsy.community.util.CarOperation)")
    public void operLogPoinCut() {
    }


    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     *
     * @param joinPoint 切入点
     * @param keys      返回结果
     */
    @AfterReturning(value = "operLogPoinCut()", returning = "keys")
    @Login
    public void saveOperLog(JoinPoint joinPoint, Object keys) {
        System.out.println("进入aop");
        CarOperationLog carOperationLog = new CarOperationLog();

        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();
        //根据uid查询用户角色id
        String userId = UserUtils.getUserId();//用户id

        Long roleId = adminUserRoleService.selectRoleIdByUserId(userId);


        // 获取操作
        try {
            CarOperation annotation = method.getAnnotation(CarOperation.class);
            if (annotation != null) {
                System.out.println("对象开始赋值");
                String operation = annotation.operation();
                System.out.println("aop获取的userRole" + roleId);
                System.out.println("aop获取的operation" + operation);

                carOperationLog.setUserRole(roleId); // 操作角色id
                carOperationLog.setOperation(operation);//操作
                carOperationLog.setStatus(0); // 状态

                carOperationLog.setOperationTime(LocalDateTime.now());
                carOperationLog.setDeleted(0L);
                System.out.println("对象结束赋值");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("开始保存");
        boolean save = carOperationService.save(carOperationLog);
        if (save) {
            carOperationLog.setStatus(1); // 状态
        }
    }
}