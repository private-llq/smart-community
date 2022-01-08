package com.jsy.community.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
/*CarOperation注解动态赋值工具*/

public class RecordCarLogUtils {


    public static void recordLog(Class currentClass,String  methodName,String operation,String userRole ,Class... parametersClass){

        System.out.println("工具赋值开始");
        try {

//            Method  method0 = currentClass.getMethod(methodName, parametersClass);
//            CarOperation annotation0 = method0.getAnnotation(CarOperation.class);
//            String value0 = annotation0.operation();//获取注解的值
//            String Role0 = annotation0.userRole();//获取注解的值
//            System.out.println("operation的值"+value0);
//            System.out.println("userRole的值"+Role0);



            Method  method = currentClass.getMethod(methodName, parametersClass);
            CarOperation annotation = method.getAnnotation(CarOperation.class);
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            Field f = invocationHandler.getClass().getDeclaredField("memberValues");
            f.setAccessible(true);
            Map memberValues = (Map) f.get(invocationHandler);
            memberValues.put("operation", operation);
            memberValues.put("userRole", userRole);
            String value = annotation.operation();//获取注解的值
            //String Role = annotation.userRole();//获取注解的值
            System.out.println("注解中operation的值"+value);
            //System.out.println("注解中userRole的值"+Role);

//            Method  method1 = currentClass.getMethod(methodName, parametersClass);
//            CarOperation annotation1 = method1.getAnnotation(CarOperation.class);
//            String value1 = annotation1.operation();//获取注解的值
//            String Role1 = annotation1.userRole();//获取注解的值
//            System.out.println("operation的值"+value1);
//            System.out.println("userRole的值"+Role1);


        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        System.out.println("工具赋值结束");
    }
}
