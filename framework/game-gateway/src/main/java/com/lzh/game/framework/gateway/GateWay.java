package com.lzh.game.framework.gateway;

import com.lzh.game.framework.gateway.config.GatewayProperties;
import com.lzh.game.framework.gateway.process.ForwardGatewayProcess;
import com.lzh.game.framework.gateway.select.factory.RandomSessionSelectFactory;
import com.lzh.game.framework.gateway.select.factory.SessionSelectFactory;
import com.lzh.game.framework.socket.core.bootstrap.AtomicLifCycle;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.tcp.TcpServer;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;

import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-10-23 18:43
 **/
public class GateWay extends AtomicLifCycle {

    private final GatewayProperties properties;

    private ServerBootstrap<ServerSocketProperties> gameServer;

    private GatewayClient client;

    private final SessionSelectFactory selectFactory;

    public GateWay(GatewayProperties properties) {
        this(properties, new RandomSessionSelectFactory());
    }

    public GateWay(GatewayProperties properties, SessionSelectFactory selectFactory) {
        this.properties = properties;
        this.selectFactory = selectFactory;
    }

    private void init() {
        properties.getClient().setBodyDateToBytes(true);
        properties.getServer().setBodyDateToBytes(true);
        client = new GatewayClient(properties);
        client.addProcessor(new FutureResponseProcess());
        if (Objects.isNull(gameServer)) {
            gameServer = new TcpServer<>(BootstrapContext.of(properties.getServer()));
            gameServer.addProcessor(new ForwardGatewayProcess(selectFactory.createSessionSelect(this.getClient())));
        }
    }

    @Override
    public void start() {
        if (running()) {
            init();
            client.start();
            gameServer.start();
            super.start();
        }
    }

    @Override
    public void asyncStart() {
        if (running()) {
            init();
            client.start();
            gameServer.asyncStart();
            super.asyncStart();
        }
    }

    public GatewayProperties getProperties() {
        return properties;
    }


    public ServerBootstrap<ServerSocketProperties> getGameServer() {
        return gameServer;
    }


    public GatewayClient getClient() {
        return client;
    }

}
