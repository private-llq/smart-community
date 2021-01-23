package com.jsy.community.annotation;

import com.jsy.community.aspectj.FieldValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列 指定字符串 验证注解
 * @author YuLF
 * @since 2021-01-12 13:49
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldValidator.class)
public @interface FieldValid {

    String message() default "{com.jsy.community.annotation.FieldValid.message}";

    String[] value();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
