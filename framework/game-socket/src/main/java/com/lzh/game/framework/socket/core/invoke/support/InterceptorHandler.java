package com.lzh.game.framework.socket.core.invoke.support;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.utils.bean.HandlerMethod;

/**
 * Request method intercept
 */
@FunctionalInterface
public interface InterceptorHandler {

    /**
     *
     * @param request
     * @param handlerMethod
     * @param args
     * @return true not call the target method
     */
    boolean isIntercept(Request request, HandlerMethod handlerMethod, Object[] args);
}
