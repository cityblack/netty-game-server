package com.lzh.game.socket.core.invoke.support;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.GameRequest;
import com.lzh.game.socket.GameResponse;
import com.lzh.game.socket.core.invoke.ExceptionHandlerMethodResolver;
import com.lzh.game.socket.core.invoke.support.ErrorHandler;
import org.springframework.core.BridgeMethodResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultErrorHandler implements ErrorHandler {

    private Map<Method, HandlerMethod> handlerMethodMap = new ConcurrentHashMap<>();
    /**
     * To resolve the method invoke exception event
     */
    private ExceptionHandlerMethodResolver methodResolver;
    /**
     *
     */
    private Object adviceInvokeBean;

    @Override
    public boolean resolveException(Exception ex, GameRequest request, GameResponse response) {
        Method method = methodResolver.resolveMethod(ex);
        if (Objects.isNull(method)) {
            return false;
        }
        Method bridgeMethod = BridgeMethodResolver.findBridgedMethod(method);
        HandlerMethod handlerMethod = handlerMethodMap.get(bridgeMethod);
        if (handlerMethod == null) {
            handlerMethod = new HandlerMethod(adviceInvokeBean, method);
            handlerMethodMap.put(bridgeMethod, handlerMethod);
        }
        Object[] args = getParam(handlerMethod.getParamsType(), ex, request, response);
        try {
            bridgeMethod.invoke(adviceInvokeBean, args);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Object[] getParam(Class<?>[] types, Exception ex, GameRequest request, GameResponse response) {
        Object[] args = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            args[i] = this.resolveProvidedArgument(types[i], ex, request, response);
        }
        return args;
    }

    private Object resolveProvidedArgument(Class<?> type, Exception ex, GameRequest request, GameResponse response) {
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
