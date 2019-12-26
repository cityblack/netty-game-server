package com.lzh.game.common.getter;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class MethodGetter implements Getter {

    private Method method;

    public MethodGetter(Method method) {
        ReflectionUtils.makeAccessible(method);
        this.method = method;
    }

    @Override
    public Object get(Object bean) {
        return ReflectionUtils.invokeMethod(method, bean);
    }

    @Override
    public Class<?> getType() {
        return method.getReturnType();
    }

    @Override
    public String name() {
        return method.getName();
    }

    public Method getMethod() {
        return method;
    }
}
