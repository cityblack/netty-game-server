package com.lzh.game.framework.socket.starter;

import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.invoke.RequestDispatch;
import com.lzh.game.framework.socket.core.filter.Filter;
import com.lzh.game.framework.socket.core.filter.FilterHandler;
import com.lzh.game.framework.socket.core.invoke.ActionRequestHandler;
import com.lzh.game.framework.socket.core.invoke.convert.InvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.protocol.Request;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpringRequestHandler implements RequestDispatch, ApplicationContextAware {

    private RequestDispatch handler;

    private FilterHandler filterHandler;

    public SpringRequestHandler(InvokeSupport actionMethodSupport
            , InvokeMethodArgumentValues invokeMethodArgumentValues
            , SpringExceptionHandler errorHandler, SpringInterceptorHandler interceptorHandler) {
        this.handler = new ActionRequestHandler(actionMethodSupport, invokeMethodArgumentValues, errorHandler, interceptorHandler);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        List<Filter> filters = context
                .getBeanProvider(Filter.class)
                .orderedStream()
                .collect(Collectors.toList());
        this.filterHandler = new FilterHandler(filters, handler);
    }

    @Override
    public void handle(Request request) {
        if (Objects.nonNull(filterHandler)) {
            filterHandler.handle(request);
        } else {
            handler.handle(request);
        }
    }
}
