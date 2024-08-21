package com.lzh.game.framework.socket.core.invoke.bean;

import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
import com.lzh.game.framework.utils.bean.HandlerMethod;
import com.lzh.game.framework.utils.bean.MethodInvoke;

/**
 * @author zehong.l
 * @since 2024-08-21 17:10
 **/
public class EnhanceInvokeImpl implements EnhanceMethodInvoke {

    private HandlerMethod method;

    private MethodInvoke invoke;

    @Override
    public Class<?>[] getParamsType() {
        return method.getParamsType();
    }

    @Override
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    @Override
    public boolean isVoid() {
        return method.isVoid();
    }

    @Override
    public Object invoke(Object... args) {
        return invoke.invoke(args);
    }

    public EnhanceInvokeImpl(HandlerMethod method, MethodInvoke invoke) {
        this.method = method;
        this.invoke = invoke;
    }
}
