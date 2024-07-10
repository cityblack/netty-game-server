package com.lzh.game.framework.logs.anno;

import com.lzh.game.framework.logs.LogScanPackages;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author zehong.l
 * @since 2024-06-13 16:58
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({LogScanPackages.Registrar.class})
public @interface EnableLog {

    @AliasFor("value")
    String[] basePackages() default {};

    @AliasFor("scan")
    String value() default "";
}
