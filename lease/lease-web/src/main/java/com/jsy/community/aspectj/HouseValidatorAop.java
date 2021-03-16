package com.jsy.community.aspectj;

import com.jsy.community.annotation.HouseValid;
import com.jsy.community.api.IHouseLeaseService;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 出租房通用验证
 * @author YuLF
 * @since 2021-03-02 11:28
 */
@Aspect
@Component
public class HouseValidatorAop extends BaseAop {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseLeaseService iHouseLeaseService;

    @Pointcut("@annotation(com.jsy.community.annotation.HouseValid)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object desensitization(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> targetClass = point.getTarget().getClass();
        Method method = getMethod(targetClass, signature);
        HouseLeaseQO qo = null;
        Object[] args = point.getArgs();
        for (Object arg : args) {
            if( arg instanceof HouseLeaseQO ){
                qo = (HouseLeaseQO) arg;
                break;
            }
        }
        if ( qo == null){
            return point.proceed();
        }
        //验证参数
        HouseValid annotation = method.getAnnotation(HouseValid.class);
        Class<?> validationInterface = annotation.validationInterface();
        //新增参数常规效验
        ValidatorUtils.validateEntity(qo, validationInterface);
        qo.setUid(UserUtils.getUserId());
        //验证所属社区所属用户房屋是否存在
        iHouseLeaseService.checkHouse(UserUtils.getUserId(), qo.getHouseCommunityId(), qo.getHouseId(), annotation.operation());
        return point.proceed();
    }


}
