package com.lzh.game.socket.core.message;

import java.lang.annotation.*;

/**
 * @author zehong.l
 * @date 2024-04-07 14:28
 **/
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Protocol {

    // msg id
    int value() default 0;

    int protocolType() default 0;

    int serializeType() default 0;
}
