package com.moudle.router_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2018/7/24.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Extra {

    String name() default "" ;
}
