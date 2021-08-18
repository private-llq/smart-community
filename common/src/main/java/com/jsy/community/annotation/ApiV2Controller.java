package com.jsy.community.annotation;

import java.lang.annotation.*;

/**
 * @program: com.jsy.community
 * @description: appv2版控制器
 * @author: Hu
 * @create: 2021-08-16 09:43
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiV2Controller {
}
