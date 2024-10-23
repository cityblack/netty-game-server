package com.lzh.game.framework.gateway;

import com.lzh.game.framework.gateway.config.GatewayProperties;
import com.lzh.game.framework.gateway.process.ForwardGatewayProcess;
import com.lzh.game.framework.gateway.process.ForwardSessionSelect;
import com.lzh.game.framework.gateway.process.RandomSessionSelect;
import com.lzh.game.framework.socket.core.bootstrap.AtomicLifCycle;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.server.AbstractServerBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.tcp.TcpServer;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;

import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-10-23 18:43
 **/
public class GateWay extends AtomicLifCycle {

    private final GatewayProperties properties;

    private AbstractServerBootstrap<GameServerSocketProperties> gameServer;

    private GatewayClient client;

    private ForwardSessionSelect sessionSelect;

    public GateWay(GatewayProperties properties) {
        this.properties = properties;
    }

    private void init() {
        properties.getClient().setBodyDateToBytes(true);
        properties.getServer().setBodyDateToBytes(true);
        client = new GatewayClient(properties);
        client.addProcessor(new FutureResponseProcess());
        if (Objects.isNull(gameServer)) {
            gameServer = new TcpServer<>(BootstrapContext.of(properties.getServer()));
            var select = Objects.nonNull(sessionSelect) ? sessionSelect : new RandomSessionSelect(client);
            gameServer.addProcessor(new ForwardGatewayProcess(select));
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


    public AbstractServerBootstrap<GameServerSocketProperties> getGameServer() {
        return gameServer;
    }


    public GatewayClient getClient() {
        return client;
    }

    public ForwardSessionSelect getSessionSelect() {
        return sessionSelect;
    }

    public void setSessionSelect(ForwardSessionSelect sessionSelect) {
        this.sessionSelect = sessionSelect;
    }
}
