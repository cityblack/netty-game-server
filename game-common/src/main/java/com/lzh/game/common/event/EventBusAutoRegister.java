package com.lzh.game.common.event;

import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * When class implement {@link SubscribeListener} with spring, Will be auto register to event bus
 */
@Slf4j
public class EventBusAutoRegister implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, SubscribeListener> beans = applicationContext.getBeansOfType(SubscribeListener.class);

        beans.forEach((k,v) -> {
            if (log.isDebugEnabled()) {
                log.debug("Register event class -> {}", v.getClass().getName());
            }
            v.beforeRegister();
            EventBus.getDefault().register(v);
            v.afterRegister();
        });
    }
}
