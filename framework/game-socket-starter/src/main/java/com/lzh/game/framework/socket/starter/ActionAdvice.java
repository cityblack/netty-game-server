package com.lzh.game.framework.socket.starter;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Deal to execute action exception
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ActionAdvice {

}
