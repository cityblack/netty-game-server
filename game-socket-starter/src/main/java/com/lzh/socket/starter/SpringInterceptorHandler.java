package com.lzh.socket.starter;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.invoke.support.ActionInterceptor;
import com.lzh.game.socket.core.invoke.support.InterceptorHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpringInterceptorHandler implements InterceptorHandler, ApplicationContextAware {

    private List<ActionInterceptor> actionInterceptors;

    @Override
    public boolean isIntercept(Request request, HandlerMethod handlerMethod, Object[] args) {
        if (Objects.isNull(actionInterceptors) || actionInterceptors.isEmpty()) {
            return false;
        }
        for (ActionInterceptor interceptor : actionInterceptors) {
            if (interceptor.intercept(request, handlerMethod.getMethod(), args)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        loadActionIntercept(applicationContext);
    }

    private void loadActionIntercept(ApplicationContext context) {
        Map<String, ActionInterceptor> beans = context.getBeansOfType(ActionInterceptor.class);
        actionInterceptors = beans.values().stream().collect(Collectors.toList());
    }
}
