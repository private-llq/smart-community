package com.jsy.community.util;


import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.AdminUserRoleService;
import com.jsy.community.api.CarOperationService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarOperationLog;
import com.jsy.community.utils.UserUtils;
import com.zhsj.baseweb.support.ContextHolder;
import com.zhsj.baseweb.support.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


/**
 * 切面处理类，操作日志异常日志记录处理
 *
 * @author arli
 * @date 2019/03/21
 */
@Aspect
@Component
@Slf4j
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
        log.info("进入aop");
        CarOperationLog carOperationLog = new CarOperationLog();
        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();
        //根据uid查询用户角色id
        LoginUser loginUser = ContextHolder.getContext().getLoginUser();
        String nickName = loginUser.getNickName();//昵称

        String userId = UserUtils.getId();
        Long adminCommunityId = UserUtils.getAdminCommunityId();
       log.info("用户id"+userId+"社区id"+adminCommunityId);
//
//        String userId = UserUtils.getId();//用户id
//        Long roleId = adminUserRoleService.selectRoleIdByUserId(userId, UserUtils.getAdminCompanyId());
        // 获取操作
        try {
            CarOperation annotation = method.getAnnotation(CarOperation.class);
            if (annotation != null) {
                System.out.println("对象开始赋值");
                String operation = annotation.operation();
//                System.out.println("aop获取的userRole" + roleId);
                System.out.println("aop获取的operation" + operation);
                carOperationLog.setUserRole(1L); // 操作角色id
                carOperationLog.setUserName(nickName);//用户名
                carOperationLog.setCommunityId(adminCommunityId);//社区id
                carOperationLog.setUserId(userId);//用户id
                carOperationLog.setOperation(operation);//操作
                carOperationLog.setStatus(1); // 状态
                carOperationLog.setOperationTime(LocalDateTime.now());
                carOperationLog.setDeleted(0L);
                System.out.println("对象结束赋值");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("开始保存车辆操作日志");
        boolean save = carOperationService.save(carOperationLog);

    }
}