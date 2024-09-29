package com.lzh.game.framework.socket.starter.server;

import com.lzh.game.framework.socket.core.bootstrap.server.AbstractServerBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServer;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.starter.SocketServerRunner;
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

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, SocketServerRunner> beans = context.getBeansOfType(SocketServerRunner.class);
        var runners = new ArrayList<>(beans.values());
        this.bootstrap.getContext().getPipeline()
                .addProcessEventListen(ProcessEvent.SERVER_START, (e, o) -> {
            for (SocketServerRunner runner : runners) {
                runner.run(bootstrap);
            }
        });
    }

    @Override
    public void start() {
        bootstrap.start();
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
