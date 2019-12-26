package com.lzh.game.socket.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Deal to execute action exception
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ControllerAdvice {

}
