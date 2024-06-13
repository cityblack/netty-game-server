package com.lzh.game.framework.logs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zehong.l
 * @since 2023-05-31 16:39
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogMethod {

    String logFile();

    int logReason() default 0;

}
