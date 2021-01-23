package com.jsy.community.aspectj;

import com.jsy.community.annotation.FieldValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 列文本验证器 只能输入指定的几个文本
 * @author YuLF
 * @since 2021-01-12 13:50
 */
public class FieldValidator implements ConstraintValidator<FieldValid, Object> {

    String[] values;


    /**
     * 验证开始前调用注解里的方法，从而获取到一些注解里的参数
     * @param constraintAnnotation  验证注解
     */
    @Override
    public void initialize(FieldValid constraintAnnotation) {
        this.values = constraintAnnotation.value();
    }

    /**
     * 判断前端值是否是指定的几个值
     * @param o     参数值
     * @param constraintValidatorContext    约束器上下文
     * @return      返回验证是否成功
     */
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        //如果该值为 null 和 "" 则不验证
        if( o == null || "".equals(o)  ){
            return true;
        }
        if(o instanceof String) {
            for (String value : values) {
                if(o.equals(value)){
                    return true;
                }
            }
        }
        return false;
    }
}
