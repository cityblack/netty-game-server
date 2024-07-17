package com.lzh.game.framework.gateway.config;

import com.lzh.game.framework.gateway.GatewayClient;
import com.lzh.game.framework.gateway.GatewayClientSessionManage;
import com.lzh.game.framework.gateway.process.ForwardSessionSelect;
import com.lzh.game.framework.gateway.process.RandomSessionSelect;
import com.lzh.game.framework.gateway.process.ForwardGatewayProcess;
import com.lzh.game.framework.socket.core.bootstrap.client.GameClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.TcpCommonServer;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;
import com.lzh.game.framework.socket.core.session.GameSessionManage;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionFactory;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.framework.socket.core.session.impl.GameSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class GateConfiguration {

    @Autowired
    private GatewayProperties properties;

    @Bean
    public TcpCommonServer<GameServerSocketProperties> gameServer() {
        properties.getServer().setUseDefaultRequest(false);
        var server = new TcpCommonServer<>(properties.getServer());
        server.setSessionManage(serverSessionManage());
        server.addProcessor(gatewayProcess());
        server.asyncStart();
        return server;
    }

    @Bean
    public GatewayClient client() {
        GatewayClient client = new GatewayClient(new GameClientSocketProperties());
        client.setSessionManage(clientSessionMange());
        client.addProcessor(new FutureResponseProcess());
        client.start();
        return client;
    }

    @Bean
    public ForwardGatewayProcess gatewayProcess() {
        ForwardSessionSelect select = new RandomSessionSelect();
        return new ForwardGatewayProcess(client(), select);
    }

    public SessionManage<Session> clientSessionMange() {
        SessionFactory<Session> factory = GameSession::of;
        return new GatewayClientSessionManage(factory);
    }

    protected SessionManage<Session> serverSessionManage() {
        SessionFactory<Session> sessionFactory = GameSession::of;
        return new GameSessionManage<>(sessionFactory, new GameSessionMemoryCacheManage<>());
    }
}