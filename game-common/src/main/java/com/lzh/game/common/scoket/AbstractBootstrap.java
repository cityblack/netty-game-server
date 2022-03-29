package com.lzh.game.common.scoket;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractBootstrap<T extends GameSocketProperties>
        implements ApplicationContextAware, SmartInitializingSingleton {

    protected T properties;

    public AbstractBootstrap(T properties) {
        this.properties = properties;
    }

    protected ApplicationContext context;

    public abstract MessageHandler messageHandler();

    @Override
    public void afterSingletonsInstantiated() {
        this.doStart();
    }

    protected ApplicationContext getContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        this.init(applicationContext);
    }

    protected abstract void init(ApplicationContext context);

    protected abstract void doStart();
}
