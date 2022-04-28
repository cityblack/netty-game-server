package com.lzh.game.socket.core.invoke;

import com.lzh.game.socket.Request;

import java.lang.reflect.Method;

/**
 * Before invoke request mapping method
 */
public interface ActionInterceptor {
    /**
     * Whether to intercept action
     * @param request
     * @param method
     * @param param
     * @return {@code true} will intercept request. and stop carried out action
     */
    boolean intercept(Request request, Method method, Object[] param);
}
