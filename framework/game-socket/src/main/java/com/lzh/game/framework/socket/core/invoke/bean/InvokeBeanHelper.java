package com.lzh.game.framework.socket.core.invoke.bean;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;

import java.lang.reflect.Method;

/**
 * @author zehong.l
 * @since 2024-08-21 15:50
 **/
public interface InvokeBeanHelper {

    InvokeModel parseBean(Object bean, Method method, BootstrapContext<?> context);

    boolean match(Object bean, Method method);
}
