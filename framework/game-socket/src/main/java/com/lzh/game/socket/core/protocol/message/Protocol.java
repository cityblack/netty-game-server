package com.lzh.game.socket.core.protocol.message;

import com.lzh.game.socket.Constant;

import java.lang.annotation.*;

/**
 * @author zehong.l
 * @since 2024-04-07 14:28
 **/
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Protocol {

    // msg id
    int value();

    int protocolType() default 0;

    int serializeType() default Constant.DEFAULT_SERIAL_SIGN;
}
