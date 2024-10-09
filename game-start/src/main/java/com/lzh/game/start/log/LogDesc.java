package com.lzh.game.start.log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author zehong.l
 * @since 2024-10-08 16:42
 **/
@Retention(RetentionPolicy.RUNTIME)
public @interface LogDesc {

    LogFile logFile();

    LogReason logReason() default LogReason.NONE;
}
