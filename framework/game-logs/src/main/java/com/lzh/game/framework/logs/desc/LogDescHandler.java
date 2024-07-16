package com.lzh.game.framework.logs.desc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author zehong.l
 * @since 2024-07-10 11:45
 **/
public interface LogDescHandler {

    LogDescDefined getDefined(Method method);
}
