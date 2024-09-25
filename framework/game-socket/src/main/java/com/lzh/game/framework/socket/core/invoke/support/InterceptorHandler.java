package com.lzh.game.framework.socket.core.invoke.support;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.common.method.EnhanceMethodInvoke;

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
    boolean isIntercept(Request request, EnhanceMethodInvoke handlerMethod, Object[] args);
}
