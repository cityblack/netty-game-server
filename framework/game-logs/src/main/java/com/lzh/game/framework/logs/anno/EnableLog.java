package com.lzh.game.framework.logs.anno;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author zehong.l
 * @since 2024-06-13 16:58
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EnableLog {

    @AliasFor("value")
    String scan();

    @AliasFor("scan")
    String value() default "";
}
