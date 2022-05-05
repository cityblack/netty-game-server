package com.lzh.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.ServerExchange;
import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.filter.FilterHandler;
import com.lzh.game.socket.core.invoke.ActionRequestHandler;
import com.lzh.game.socket.core.invoke.InvokeMethodArgumentValues;
import com.lzh.game.socket.core.invoke.RequestActionSupport;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpringRequestHandler implements RequestHandler, ApplicationContextAware {

    private RequestHandler handler;

    private FilterHandler filterHandler;

    public SpringRequestHandler(RequestActionSupport<EnhanceHandlerMethod> actionMethodSupport
            , InvokeMethodArgumentValues<Request> invokeMethodArgumentValues
            , SpringExceptionHandler errorHandler, SpringInterceptorHandler interceptorHandler) {
        this.handler = new ActionRequestHandler(actionMethodSupport, invokeMethodArgumentValues, errorHandler, interceptorHandler);
    }

    @Override
    public void handler(ServerExchange exchange) {
        if (Objects.nonNull(filterHandler)) {
            filterHandler.handler(exchange);
        } else {
            handler.handler(exchange);
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
