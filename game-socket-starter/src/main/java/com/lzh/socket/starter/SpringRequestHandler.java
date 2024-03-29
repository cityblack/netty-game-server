package com.lzh.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.ActionMethodSupport;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.RemoteContext;
import com.lzh.game.socket.core.RequestHandle;
import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.filter.FilterHandler;
import com.lzh.game.socket.core.invoke.ActionRequestHandler;
import com.lzh.game.socket.core.invoke.InvokeMethodArgumentValues;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpringRequestHandler implements RequestHandle, ApplicationContextAware {

    private RequestHandle handler;

    private FilterHandler filterHandler;

    public SpringRequestHandler(ActionMethodSupport<EnhanceHandlerMethod> actionMethodSupport
            , InvokeMethodArgumentValues<Request> invokeMethodArgumentValues
            , SpringExceptionHandler errorHandler, SpringInterceptorHandler interceptorHandler) {
        this.handler = new ActionRequestHandler(actionMethodSupport, invokeMethodArgumentValues, errorHandler, interceptorHandler);
    }

    @Override
    public void handle(RemoteContext exchange) {
        if (Objects.nonNull(filterHandler)) {
            filterHandler.handle(exchange);
        } else {
            handler.handle(exchange);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        List<Filter> filters = context
                .getBeanProvider(Filter.class)
                .orderedStream()
                .collect(Collectors.toList());
        this.filterHandler = new FilterHandler(filters, handler);
    }
}
