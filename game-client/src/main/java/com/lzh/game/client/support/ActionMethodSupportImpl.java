package com.lzh.game.client.support;

import com.lzh.game.client.Action;
import com.lzh.game.common.bean.HandlerMethod;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ActionMethodSupportImpl implements ActionMethodSupport, ApplicationContextAware {

    private Map<Integer, HandlerMethod> methodMap = new ConcurrentHashMap<>();

    @Override
    public HandlerMethod getMethod(int cmd) {
        return methodMap.get(cmd);
    }

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
                        register(cmd, new HandlerMethod(e, method));
                    }, method -> method.isAnnotationPresent(Response.class));
                });
    }
}
