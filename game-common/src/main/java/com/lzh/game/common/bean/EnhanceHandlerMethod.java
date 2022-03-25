package com.lzh.game.common.bean;

import java.lang.reflect.Method;

public class EnhanceHandlerMethod extends HandlerMethod {

    private MethodInvoke invoke;

    public EnhanceHandlerMethod(Object bean, Method method) {
        super(bean, method);
        this.init(bean, method);
    }

    private void init(Object bean, Method method) {
        try {
            this.invoke = MethodInvokeUtils.enhanceInvoke(bean, method);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Object doInvoke(Object... args) throws Exception {
        return invoke.invoke(args);
    }
}
