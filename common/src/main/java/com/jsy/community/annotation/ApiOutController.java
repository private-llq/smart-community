package com.jsy.community.annotation;

import java.lang.annotation.*;

/**
* @Description: 外部访问接口(非APP)
 * @Author: chq459799974
 * @Date: 2021/1/11
**/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiOutController {
}
