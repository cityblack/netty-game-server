package com.lzh.game.socket.dispatcher.action;

import com.lzh.game.socket.exchange.Request;

import java.lang.reflect.Method;

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
