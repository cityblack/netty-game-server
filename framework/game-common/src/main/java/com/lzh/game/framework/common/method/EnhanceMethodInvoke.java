package com.lzh.game.framework.common.method;

import java.lang.reflect.Method;

/**
 * @author zehong.l
 * @since 2024/6/21 22:40
 **/
public interface EnhanceMethodInvoke extends MethodInvoke {

    /**
     * Params type
     * @return The method param type list
     */
    Class<?>[] getParamsType();

    /**
     * @return The method return type
     */
    Class<?> getReturnType();

    /**
     *
     * @return if true. The method return type is Void
     */
    boolean isVoid();

    Method method();
}
