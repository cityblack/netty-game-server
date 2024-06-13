package com.lzh.game.framework.socket.starter;

import com.lzh.game.framework.socket.core.invoke.MethodInvoke;
import com.lzh.game.framework.socket.core.invoke.support.ErrorHandler;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;
import org.springframework.core.BridgeMethodResolver;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultErrorHandler implements ErrorHandler {

    private Map<Method, MethodInvoke> handlerMethodMap = new ConcurrentHashMap<>();
    /**
     * To resolve the method invoke exception event
     */
    private ExceptionHandlerMethodResolver methodResolver;
    /**
     *
     */
    private Object adviceInvokeBean;

    public DefaultErrorHandler(ExceptionHandlerMethodResolver methodResolver, Object adviceInvokeBean) {
        this.methodResolver = methodResolver;
        this.adviceInvokeBean = adviceInvokeBean;
    }

    @Override
    public boolean resolveException(Exception ex, Request request, Response response) {
        Method method = methodResolver.resolveMethod(ex);
        if (Objects.isNull(method)) {
            return false;
        }
        Method bridgeMethod = BridgeMethodResolver.findBridgedMethod(method);
        MethodInvoke handlerMethod = handlerMethodMap.get(bridgeMethod);
        if (handlerMethod == null) {
            handlerMethod = new HandlerMethod(adviceInvokeBean, method);
            handlerMethodMap.put(bridgeMethod, handlerMethod);
        }
        Object[] args = getParam(handlerMethod.getParamsType(), ex, request, response);
        handlerMethod.invoke(adviceInvokeBean, args);
        return true;
    }

    private Object[] getParam(Class<?>[] types, Exception ex, Request request, Response response) {
        Object[] args = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            args[i] = this.resolveProvidedArgument(types[i], ex, request, response);
        }
        return args;
    }

    private Object resolveProvidedArgument(Class<?> type, Exception ex, Request request, Response response) {
        Object[] values = new Object[]{ex, request, response};

        for (int i = 0; i < values.length; i++) {
            Object providedArg = values[i];
            if (type.isInstance(providedArg)) {
                return providedArg;
            }
        }

        return null;
    }
}
