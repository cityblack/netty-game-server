package com.lzh.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.core.invoke.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Action parse
 */
@Slf4j
public class SpringActionSupport implements RequestActionSupport<EnhanceHandlerMethod>, ApplicationContextAware {

    private RequestActionSupport<EnhanceHandlerMethod> support;

    private ConvertManager convertManager;

    public SpringActionSupport(RequestActionSupport<EnhanceHandlerMethod> support, ConvertManager convertManager) {
        this.support = support;
        this.convertManager = convertManager;
    }

    @Override
    public EnhanceHandlerMethod getActionHandler(int cmd) {
        return support.getActionHandler(cmd);
    }

    @Override
    public boolean containMapping(int cmd) {
        return support.containMapping(cmd);
    }

    @Override
    public void register(int cmd, EnhanceHandlerMethod method) {
        support.register(cmd, method);
        registerConvert(method);
    }

    private void registerConvert(EnhanceHandlerMethod method) {
        Class<?>[] paramsType = method.getParamsType();
        for (Class<?> target : paramsType) {
            if (convertManager.hasConvert(target)) {
                continue;
            }
            ParamConvert<?> convert = new ProtoBufferConvert<>(target);
            convertManager.registerConvert(target, convert, false);
        }
    }

    @Override
    public int getRequestRelation(int requestCmd) {
        return support.getRequestRelation(requestCmd);
    }

    @Override
    public void register(int requestCmd, EnhanceHandlerMethod method, int responseCmd) {
        support.register(requestCmd, method, responseCmd);
        registerConvert(method);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.parseActionHandler(applicationContext);
    }

    private void parseActionHandler(ApplicationContext applicationContext) {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(Action.class);
        Set<Class<?>> inner = convertManager.inner();
        if (!beanMap.isEmpty()) {
            synchronized (DefaultActionMethodSupport.class) {
                beanMap.forEach((k, v) -> {
                    Class<?> clazz = v.getClass();
                    if (clazz.isInterface()) {
                        throw new IllegalArgumentException("@Action can't use to interface.");
                    }
                    List<InvokeUtils.InvokeModel> models = InvokeUtils.parseBean(v, inner);
                    for (InvokeUtils.InvokeModel model : models) {
                        int requestCmd = model.getValue();
                        EnhanceHandlerMethod method = model.getHandlerMethod();
                        if (containMapping(requestCmd)) {
                            throw new IllegalArgumentException("The cmd not unique." + model.getResponse());
                        }
                        if (model.hasResponse()) {
                            register(requestCmd, method, model.getResponse());
                        } else {
                            register(requestCmd, method);
                        }
                    }
                });
            }
        } else {
            if (log.isWarnEnabled()) {
                log.warn("No action defined!");
            }
        }
    }
}
