package com.log.component.aop;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ControllerLogger {

    String operModul() default ""; // 操作模块

    String operType() default "";  // 操作类型

    String operDesc() default "";  // 操作说明
}
