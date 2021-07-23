package com.jsy.community.utils;

import com.jsy.community.qo.proprietor.OldPushInformQO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author YuLF
 * @since 2021-01-08 16:55
 */
@Aspect
@Component
public class SysAop {

    @Value("${jsy.sys.inform-name}")
    private String sysInformName;

    @Value("${jsy.sys.inform-icon}")
    private String sysInformIcon;

    @Value("${jsy.sys.inform-id}")
    private Long sysInformId;

    @Pointcut(value = "execution(* com.jsy.community.controller.SysInformController.add(..))")
    public void sysInformAdd()
    {
    }

    /**
     * 对新增 系统消息 推送时 方法执行前 切入 设置 系统消息基本信息、如：后台定义的系统推送号名称、系统消息头像地址...
     */
    @Before( "sysInformAdd()" )
    public void sysInformAddStart( JoinPoint joinPoint )
    {
        Object[] args = joinPoint.getArgs();
        OldPushInformQO qo = (OldPushInformQO) args[0];
        qo.setAcctId(this.sysInformId);
        qo.setAcctAvatar(this.sysInformIcon);
        qo.setAcctName(this.sysInformName);
        //0表示该消息推送至所有社区
        qo.setPushTarget(0);
    }


}
