package com.lzh.socket.starter;

import com.lzh.game.socket.core.invoke.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;

/**
 * Load Spring bean parse to invoker
 *
 * @Action parse
 */
@Slf4j
public class SpringActionSupport implements InvokeSupport, ApplicationContextAware {

    private InvokeSupport support;

    private RequestConvertManager requestConvertManager;

    public SpringActionSupport(InvokeSupport support, RequestConvertManager requestConvertManager) {
        this.support = support;
        this.requestConvertManager = requestConvertManager;
    }

    @Override
    public MethodInvoke getActionHandler(int cmd) {
        return support.getActionHandler(cmd);
    }

    @Override
    public boolean containMapping(int cmd) {
        return support.containMapping(cmd);
    }

    @Override
    public void register(int cmd, MethodInvoke method) {
        support.register(cmd, method);
        registerConvert(method);
    }

    @Override
    public List<MethodInvoke> getAllActionHandler() {
        return support.getAllActionHandler();
    }

    private void registerConvert(MethodInvoke method) {
        Class<?>[] paramsType = method.getParamsType();
        for (Class<?> target : paramsType) {
            if (requestConvertManager.hasConvert(target)) {
                continue;
            }
            RequestConvert<?> convert = new ProtoBufferConvert<>(target);
            requestConvertManager.registerConvert(target, convert);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.parseActionHandler(applicationContext);
    }

    private void parseActionHandler(ApplicationContext applicationContext) {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(Action.class);
        if (!beanMap.isEmpty()) {
            synchronized (DefaultActionInvokeSupport.class) {
                beanMap.forEach((k, v) -> {
                    Class<?> clazz = v.getClass();
                    if (clazz.isInterface()) {
                        throw new IllegalArgumentException("@Action can't use to interface.");
                    }
                    List<InvokeUtils.InvokeModel> models = InvokeUtils.parseBean(v);
                    for (InvokeUtils.InvokeModel model : models) {
                        int requestCmd = model.getValue();
                        EnhanceHandlerMethod method = model.getHandlerMethod();
                        if (containMapping(requestCmd)) {
                            throw new IllegalArgumentException("The cmd not unique." + requestCmd);
                        }
                        register(requestCmd, method);
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
