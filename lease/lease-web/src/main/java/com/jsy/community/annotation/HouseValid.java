package com.jsy.community.annotation;

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


}
