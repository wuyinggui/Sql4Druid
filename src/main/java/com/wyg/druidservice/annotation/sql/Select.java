package com.wyg.druidservice.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {
    String value();
    String dynamicReplaceMark() default "#{dynamic}";
    String timeformatReplaceMark() default "#{timeformat}";
    Class[] resultTypes() default {};
    String[] columnLabels() default {};
}
