package com.lzh.game.framework.socket.starter.bean;

import com.lzh.game.framework.socket.core.invoke.support.ActionInterceptor;
import com.lzh.game.framework.socket.core.invoke.support.InterceptorHandler;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.common.method.EnhanceMethodInvoke;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpringInterceptorHandler implements InterceptorHandler, ApplicationContextAware {

    private List<ActionInterceptor> actionInterceptors;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        loadActionIntercept(applicationContext);
    }

    private void loadActionIntercept(ApplicationContext context) {
        Map<String, ActionInterceptor> beans = context.getBeansOfType(ActionInterceptor.class);
        actionInterceptors = new ArrayList<>(beans.values());
    }

    @Override
    public boolean isIntercept(Request request, EnhanceMethodInvoke handlerMethod, Object[] args) {
        if (Objects.isNull(actionInterceptors) || actionInterceptors.isEmpty()) {
            return false;
        }
        for (ActionInterceptor interceptor : actionInterceptors) {
            if (interceptor.intercept(request, handlerMethod, args)) {
                return true;
            }
        }
        return false;
    }
}
