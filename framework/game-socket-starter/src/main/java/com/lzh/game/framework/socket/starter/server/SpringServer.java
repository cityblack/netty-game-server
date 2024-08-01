package com.lzh.game.framework.socket.starter.server;

import com.lzh.game.framework.socket.core.bootstrap.server.AbstractServerBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServer;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.starter.SocketServerInit;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpringServer<T extends GameServerSocketProperties> implements GameServer, ApplicationContextAware {

    private final AbstractServerBootstrap<T> bootstrap;

    public SpringServer(AbstractServerBootstrap<T> bootstrap) {
        this.bootstrap = bootstrap;
    }

    private List<SocketServerInit> inits;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, SocketServerInit> beans = context.getBeansOfType(SocketServerInit.class);
        List<SocketServerInit> list = new ArrayList<>(beans.size());
        for (Map.Entry<String, SocketServerInit> entry : beans.entrySet()) {
            list.add(entry.getValue());
        }
        this.inits = list;
    }

    @Override
    public void start() {
        bootstrap.start();
        if (Objects.nonNull(inits)) {
            for (SocketServerInit init : this.inits) {
                init.doInit(bootstrap.getProperties(), bootstrap);
            }
        }
    }

    @Override
    public boolean isStared() {
        return bootstrap.isStared();
    }

    @Override
    public void shutDown() {
        bootstrap.asyncStart();
    }

    @Override
    public void asyncStart() {
        bootstrap.asyncStart();
    }

    @Override
    public int getPort() {
        return bootstrap.getPort();
    }
}
