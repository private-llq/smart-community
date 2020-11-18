package com.jsy.community.annotation.web;

import java.lang.annotation.*;

/**
 * 后台注解
 *
 * @author ling
 * @since 2020-11-18 09:58
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiAdmin {
}
