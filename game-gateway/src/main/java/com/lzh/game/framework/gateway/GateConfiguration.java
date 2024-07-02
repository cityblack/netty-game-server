package com.lzh.game.framework.gateway;

import com.lzh.game.framework.socket.core.bootstrap.server.GameServer;
import com.lzh.game.framework.socket.GameSocketProperties;
import com.lzh.game.framework.socket.core.ForwardSessionSelect;
import com.lzh.game.framework.socket.core.bootstrap.client.GameTcpClient;
import com.lzh.game.framework.socket.core.bootstrap.server.TcpCommonServer;
import com.lzh.game.framework.socket.core.process.impl.ForwardGatewayProcess;
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
    public GameServer gameServer() {

        var server = new TcpCommonServer<>(serverSocketProperties, serverSessionManage());
        server.setDispatch(requestHandle)
                .setMethodSupport(actionSupport)
                .setConvertManager(requestConvertManager)
                .setArgumentValues(argumentValues);

        server.asyncStart();
        return server;
    }

    @Bean
    public GameTcpClient client() {
        GatewayClient client = new GatewayClient(new GameSocketProperties(), properties, clientSessionMange());
        FutureResponseProcess process = new FutureResponseProcess();

        client.start();
        return client;
    }

    @Bean
    public ForwardGatewayProcess gatewayProcess(GameTcpClient client) {
        ForwardSessionSelect select = new RandomSessionSelect();
        return new ForwardGatewayProcess(client, select, client.getService());
    }

    public SessionManage<Session> clientSessionMange() {
        SessionFactory<Session> factory = GameSession::of;
        return new GatewaySessionManage(factory);
    }

    protected SessionManage<GameSession> serverSessionManage() {
        SessionFactory<GameSession> sessionFactory = GameSession::of;
        return new GameSessionManage<>(sessionFactory, new GameSessionMemoryCacheManage<>());
    }
}
