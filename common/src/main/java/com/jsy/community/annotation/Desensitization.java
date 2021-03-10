package com.jsy.community.annotation;
import com.jsy.community.aspectj.DesensitizationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脱敏注解
 * 使用例加方法上面：@Desensitization(type = {DesensitizationType.ID_CARD,DesensitizationType.PHONE,DesensitizationType.EMAIL}, field = {"idCard","phone","email"})
 * 上述表示针对当前方法返回值的属性 idCard、phone、email进行脱敏
 * 脱敏类型与 对象属性字段需要一一对应
 * 脱敏支持切入返回值类型：CommonResult<List<Bean>>、CommonResult<Bean>、List<Bean>、Bean、里面key为list的List<Bean>数据的Map对象
 * @author YuLF
 * @since  2021/1/23 10:36
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Desensitization {

    /**
     * 脱敏规则类型
     */
    DesensitizationType[] type();

    /**
     * 脱敏类型对应的字段
     */
    String[] field();

}
