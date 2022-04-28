package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.ProcessListener;
import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.bean.HandlerMethod;

import com.lzh.game.socket.annotation.ControllerAdvice;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.Response;
import com.lzh.game.socket.core.ServerExchange;
import com.lzh.game.socket.GameRequest;
import com.lzh.game.socket.GameResponse;
import com.lzh.game.socket.exception.NotDefinedResponseProtocolException;
import com.lzh.game.socket.exception.NotFondProtocolException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ActionRequestHandler implements RequestHandler, ApplicationContextAware {

    private final int ERROR_PROTOCOL_CODER = 0;
    /**
     * To resolve the method invoke exception event
     */
    private ExceptionHandlerMethodResolver methodResolver;
    /**
     *
     */
    private Object adviceInvokeBean;
    /**
     * To intercept action
     */
    private List<ActionInterceptor> actionInterceptors;

    private Map<Method, HandlerMethod> handlerMethodMap = new ConcurrentHashMap<>();

    private InvokeMethodArgumentValues<Request> transfer;

    private RequestActionSupport<EnhanceHandlerMethod> support;

    public ActionRequestHandler(RequestActionSupport<EnhanceHandlerMethod> support, InvokeMethodArgumentValues<Request> transfer) {
        this.support = support;
        this.transfer = transfer;
    }

    protected void executeAction(ServerExchange exchange, ProcessListener<ServerExchange> listener) {

        GameResponse response = (GameResponse) exchange.getResponse();
        GameRequest request = (GameRequest) exchange.getRequest();
        int cmd = request.cmd();

        if (!support.containMapping(cmd)) {
            listener.error(new NotFondProtocolException(cmd));
            return;
        }

        EnhanceHandlerMethod method = support.getActionHandler(cmd);

        try {
            Object o = invokeForRequest(request, method);
            // If return value isn't null. Check response cmd.
            if (Objects.nonNull(o)) {
                int responseCMD = support.getRequestRelation(cmd);
                if (responseCMD == ERROR_PROTOCOL_CODER) {
                    listener.error(new NotDefinedResponseProtocolException(cmd));
                    return;
                }
                response.setCmd(responseCMD);
                response.setData(o);
            }
            listener.success(exchange);
        } catch (Exception e) {
            boolean resolved = resolveException(e, request, response);
            if (resolved) {
                listener.success(exchange);
            } else {
                listener.error(e);
            }
        }
    }

    private Object invokeForRequest(Request request, HandlerMethod handlerMethod) throws Exception {
        Object[] args = transfer.transfer(request, handlerMethod);
        if (isIntercept(request, handlerMethod, args)) {
            return null;
        }
        Object returnValue = handlerMethod.doInvoke(args);
        return returnValue;
    }

    private boolean isIntercept(Request request, HandlerMethod handlerMethod, Object[] args) {
        return actionInterceptors.stream().map(e -> e.intercept(request, handlerMethod.getMethod(), args)).anyMatch(e -> e);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        loadAdvice(applicationContext);
        loadActionIntercept(applicationContext);
    }

    private void loadActionIntercept(ApplicationContext context) {
        Map<String, ActionInterceptor> beans = context.getBeansOfType(ActionInterceptor.class);
        actionInterceptors = beans.values().stream().collect(Collectors.toList());
    }

    private void loadAdvice(ApplicationContext context) {
        Map<String, Object> map = context.getBeansWithAnnotation(ControllerAdvice.class);
        if (map.size() > 1) {
            throw new IllegalArgumentException("@ControllerAdvice has multiple instance. " + map);
        }
        if (!map.isEmpty()) {
            map.forEach((k, v) -> {
                Class<?> clazz = v.getClass();
                methodResolver = new ExceptionHandlerMethodResolver(clazz);
                //resolver.resolveMethodByThrowable()
                adviceInvokeBean = v;
            });
        }

    }

    private boolean resolveException(Exception ex, GameRequest request, GameResponse response) {
        Method method = methodResolver.resolveMethod(ex);
        if (method != null) {
            Method bridgeMethod = BridgeMethodResolver.findBridgedMethod(method);
            HandlerMethod handlerMethod = handlerMethodMap.get(bridgeMethod);
            if (handlerMethod == null) {
                handlerMethod = new HandlerMethod(adviceInvokeBean, method);
                handlerMethodMap.put(bridgeMethod, handlerMethod);
            }
            Object[] args = getParam(handlerMethod.getMethodParameters(), ex, request, response);
            try {
                bridgeMethod.invoke(adviceInvokeBean, args);
                return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    private Object[] getParam(MethodParameter[] parameters, Exception ex, GameRequest request, GameResponse response) {
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            args[i] = this.resolveProvidedArgument(parameter, ex, request, response);
        }
        return args;
    }

    @Nullable
    private Object resolveProvidedArgument(MethodParameter parameter, Exception ex, GameRequest request, GameResponse response) {
        Object[] var3 = new Object[]{ex, request, response};
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Object providedArg = var3[var5];
            if (parameter.getParameterType().isInstance(providedArg)) {
                return providedArg;
            }
        }

        return null;
    }

    @Override
    public void handler(ServerExchange exchange) {

        executeAction(exchange, new ProcessListener<ServerExchange>() {

            @Override
            public void success(ServerExchange serverExchange) {
                Response response = serverExchange.getResponse();
                serverExchange.getSession().write(response.data());
            }

            @Override
            public void error(Throwable throwable) {
                if (throwable instanceof NotDefinedResponseProtocolException) {
                    log.error("Not register response cmd:{}", exchange.getRequest().cmd());
                } else if (throwable instanceof NotFondProtocolException) {
                    log.error("Not register request cmd:{}", exchange.getRequest().cmd());
                }
            }
        });
    }
}
