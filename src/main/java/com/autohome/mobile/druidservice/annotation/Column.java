package com.autohome.mobile.druidservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * 字段名称
     */
    String value() default "";
    boolean istimecolumn() default false;
    boolean nullable() default false;
    String operator() default "=";
    String refercolumn() default "";
}
