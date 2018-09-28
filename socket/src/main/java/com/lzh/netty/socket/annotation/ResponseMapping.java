package com.lzh.netty.socket.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseMapping {

    int value() default -1;
    /**
     * Describe the response what to do
     * @return
     */
    String desc() default "";
}
