package com.lzh.game.framework.logs.desc;

import java.lang.annotation.Annotation;

/**
 * @author zehong.l
 * @since 2024-07-10 11:45
 **/
public interface LogDescHandler<T extends Annotation> {

    T descAnno();

    LogDescDefined getDefined(T anno);
}
