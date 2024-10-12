package com.lzh.game.framework.repository.anno;

import com.lzh.game.framework.repository.Constant;

import java.lang.annotation.*;

/**
 * @author zehong.l
 * @since 2024-10-12 12:16
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cached {

    int size() default Constant.CACHE_SIZE;

    long persistenceInterval() default Constant.PERSISTENCE_INTERVAL;

    long expire() default Constant.CACHE_EXPIRE;
}
