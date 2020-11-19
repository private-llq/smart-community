package com.jsy.community.annotation;

import java.lang.annotation.*;

/**
 * 控制器注解
 *
 * @author ling
 * @since 2020-11-19 15:51
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiJSYController {
}
