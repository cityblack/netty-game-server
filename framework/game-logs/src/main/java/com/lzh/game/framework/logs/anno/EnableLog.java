package com.lzh.game.framework.logs.anno;

import com.lzh.game.framework.logs.LogConfig;
import com.lzh.game.framework.logs.LogScanPackages;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zehong.l
 * @since 2024-06-13 16:58
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({LogScanPackages.Registrar.class, LogConfig.class})
public @interface EnableLog {

    String[] value();
}
