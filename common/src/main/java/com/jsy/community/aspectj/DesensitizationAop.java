package com.jsy.community.aspectj;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.Desensitization;
import com.jsy.community.exception.JSYException;
import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 身份证、电话号码、姓名、脱敏
 * @author YuLF
 * @since 2021-01-23 11:05
 */
@Slf4j
@Aspect
@Component
public class DesensitizationAop extends BaseAop {

    @Pointcut("@annotation(com.jsy.community.annotation.Desensitization)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object desensitization(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> targetClass = point.getTarget().getClass();
        Method method = getMethod(targetClass, signature);
        Object proceed = point.proceed();
        Desensitization annotation = method.getAnnotation(Desensitization.class);
        String[] fields = annotation.field();
        DesensitizationType[] type = annotation.type();
        if (fields.length != type.length) {
            throw new JSYException("DesensitizationType 长度不匹配字段长度, 请一一对应!");
        }
        boolean isCommonResult = proceed instanceof CommonResult;
        String msg = null;
        //去壳
        if(isCommonResult){
            msg = getMegByCommonResult(proceed);
            proceed = getDataByCommonResult(proceed);
        }
        //Map<String,Object> 类型  数据字段key必须为list
        if( proceed instanceof Map){
            @SuppressWarnings("unchecked")
            Map<String,Object> map = (Map<String,Object>)proceed;
            Object list = map.get("list");
            if( Objects.nonNull(list) ){
                List<Object> objects = castList(list);
                convert(objects, fields, type);
            }
        }
        //List<Bean> 类型
        if (proceed instanceof List<?>) {
            List<?> list = (List<?>) proceed;
            convert(list, fields, type);
        } else {
            //Object类型
            resolverList(proceed, fields, type);
        }
        //加壳
        if(isCommonResult){
            return CommonResult.ok(proceed, msg);
        }
        return proceed;
    }

    private List<Object> castList(Object obj)
    {
        if(obj instanceof List<?>)
        {
            return new ArrayList<>((List<?>) obj);
        }
        return null;
    }

    private void convert(List<?> data, String[] fields, DesensitizationType[] type){
        data.forEach(obj -> resolverList(obj, fields, type));
    }


    private Object getDataByCommonResult(Object proceed) {
        if (proceed instanceof CommonResult) {
            return ((CommonResult<?>) proceed).getData();
        } else {
            return proceed;
        }
    }

    private String getMegByCommonResult(Object proceed) {
        return ((CommonResult<?>) proceed).getMessage();
    }

    private void resolverList(Object obj, String[] fields, DesensitizationType[] type) {
        List<String> listFields = Arrays.asList(fields);
        Class<?> c = obj.getClass();
        Field[] declaredFields = c.getDeclaredFields();
        for (Field f : declaredFields) {
            for (String fieldStr : listFields) {
                if (fieldStr.equals(f.getName())) {
                    f.setAccessible(true);
                    int fieldIndex = getIndex(listFields, f.getName());
                    DesensitizationType desensitizationType = type[fieldIndex];
                    try {
                        String value = f.get(obj) != null ? f.get(obj).toString() : null;
                        if (value != null) {
                            value = value.replaceAll(desensitizationType.regex[0], desensitizationType.regex[1]);
                            f.set(obj, value);
                            break;
                        }
                    } catch (IllegalAccessException e) {
                        throw new JSYException("DesensitizationType 脱敏解析结果失败!");
                    }
                }
            }
        }
    }

    private int getIndex(List<String> fields, String name) {
        int index;
        for (index = 0; index < fields.size(); index++) {
            if (fields.get(index).equals(name)) {
                break;
            }
        }
        return index;
    }


}



