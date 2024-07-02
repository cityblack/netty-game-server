package com.lzh.game.framework.socket.core.invoke;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 协议对应的方法注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Receive {

    /**
     * If the value equals zero will auto produce a MessageDefined
     * @return msg id
     */
    short value() default 0;
}
