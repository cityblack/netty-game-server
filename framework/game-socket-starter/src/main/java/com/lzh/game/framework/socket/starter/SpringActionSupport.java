package com.lzh.game.framework.socket.starter;

import com.lzh.game.framework.socket.core.bootstrap.server.AbstractServerBootstrap;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.starter.anno.Action;
import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
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

    public SpringActionSupport(InvokeSupport support) {
        this.support = support;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.parseActionHandler(applicationContext);
    }

    private void parseActionHandler(ApplicationContext applicationContext) {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(Action.class);
        if (!beanMap.isEmpty()) {
            var server = applicationContext.getBean(AbstractServerBootstrap.class);
            synchronized (SpringActionSupport.class) {
                beanMap.forEach((k, v) -> {
                    Class<?> clazz = v.getClass();
                    if (clazz.isInterface()) {
                        throw new IllegalArgumentException("@Action can't use to interface.");
                    }
                    server.addInvokeBean(v);
//                    List<InvokeUtils.InvokeModel> models = InvokeUtils.parseBean(v);
//                    for (InvokeUtils.InvokeModel model : models) {
//                        int msgId = model.getValue();
//                        var method = model.getHandlerMethod();
//                        if (containMapping(msgId)) {
//                            throw new IllegalArgumentException("The msgId not unique." + msgId);
//                        }
//                        register(msgId, method);
//                    }
                });
            }
        } else {
            if (log.isWarnEnabled()) {
                log.warn("No action defined!");
            }
        }
    }

    @Override
    public EnhanceMethodInvoke getActionHandler(short msgId) {
        return support.getActionHandler(msgId);
    }

    @Override
    public boolean containMapping(short msgId) {
        return support.containMapping(msgId);
    }

    @Override
    public void register(short msgId, EnhanceMethodInvoke invoke) {
        support.register(msgId, invoke);
    }

    @Override
    public List<EnhanceMethodInvoke> getAllActionHandler() {
        return support.getAllActionHandler();
    }
}
