package com.jsy.community.annotation;

import com.jsy.community.utils.es.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对于 整租新增、更新。合租新增、更新。单间新增、更新 通用验证器
 * 同一种验证方式，只是验证组不一样
 * @author YuLF
 * @since 2021-03-02 11:21
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HouseValid {

    /**
     * 验证组，指定qo对象的验证接口
     */
    Class<?> validationInterface();

    /**
     * 操作符 用来证明本次操作属于增删改哪一种操作，以便用来做一些其他的操作
     */
    Operation operation();


}
