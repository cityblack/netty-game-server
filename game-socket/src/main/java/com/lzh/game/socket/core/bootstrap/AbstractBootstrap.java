package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.config.GameSocketProperties;
import com.lzh.game.socket.core.MessageHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractBootstrap<T extends GameSocketProperties>
        implements ApplicationContextAware, SmartInitializingSingleton, GameServer {

    protected T properties;

    public AbstractBootstrap(T properties) {
        this.properties = properties;
    }

    protected ApplicationContext context;

    public abstract MessageHandler messageHandler();

    @Override
    public void afterSingletonsInstantiated() {
        this.start();
    }

    protected ApplicationContext getContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        this.init(applicationContext);
    }

    @Override
    public int getPort() {
        return properties.getPort();
    }

    protected abstract void init(ApplicationContext context);
}
