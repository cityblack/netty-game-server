package com.lzh.game.framework.socket.core.invoke.support;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;

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
    boolean intercept(Request request, EnhanceMethodInvoke method, Object[] param);
}
