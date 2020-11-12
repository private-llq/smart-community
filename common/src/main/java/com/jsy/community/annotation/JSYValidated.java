package com.jsy.community.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JSYValidated {
	boolean ignore() default false;
}
