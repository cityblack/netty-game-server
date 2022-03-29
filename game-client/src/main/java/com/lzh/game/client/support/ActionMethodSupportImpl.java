package com.lzh.game.client.support;

import com.lzh.game.client.Action;
import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.common.scoket.ActionMethodSupport;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActionMethodSupportImpl implements ActionMethodSupport<HandlerMethod>, ApplicationContextAware {

    private Map<Integer, HandlerMethod> methodMap = new ConcurrentHashMap<>();

    @Override
    public void register(int cmd, HandlerMethod method) {
        methodMap.put(cmd, method);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext
                .getBeansWithAnnotation(Action.class)
                .values()
                .stream()
                .forEach(e -> {
                    Class<?> clazz = e.getClass();
                    ReflectionUtils.doWithMethods(clazz, method -> {
                        Response response = method.getAnnotation(Response.class);
                        int cmd = response.value();
                        EnhanceHandlerMethod invoke = new EnhanceHandlerMethod(e, method);
                        register(cmd, invoke);
                    }, method -> method.isAnnotationPresent(Response.class));
                });
    }

    @Override
    public HandlerMethod getActionHandler(int cmd) {
        return methodMap.get(cmd);
    }

    @Override
    public boolean containMapping(int cmd) {
        return this.methodMap.containsKey(cmd);
    }
}
