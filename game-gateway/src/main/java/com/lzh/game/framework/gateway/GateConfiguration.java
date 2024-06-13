package com.lzh.game.framework.gateway;

import com.lzh.game.framework.socket.core.bootstrap.GameServer;
import com.lzh.game.framework.socket.GameSocketProperties;
import com.lzh.game.framework.socket.core.ForwardSessionSelect;
import com.lzh.game.framework.socket.core.process.RequestDispatch;
import com.lzh.game.framework.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.framework.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.framework.socket.core.invoke.RequestConvertManager;
import com.lzh.game.framework.socket.core.invoke.InvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.process.ForwardGatewayProcess;
import com.lzh.game.framework.socket.core.process.FutureResponseProcess;
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
import org.springframework.context.annotation.Import;


@Configuration
@Import(GameSocketConfiguration.class)
@EnableConfigurationProperties(GatewayProperties.class)
public class GateConfiguration {

    @Autowired
    private GatewayProperties properties;

    @Bean
    public GameServer gameServer(SpringGameServerProperties serverSocketProperties
            , RequestDispatch requestHandle
            , InvokeSupport<EnhanceHandlerMethod> actionSupport
            , RequestConvertManager requestConvertManager
            , InvokeMethodArgumentValues argumentValues, ForwardGatewayProcess gatewayProcess) {

        TcpCommonServer server = new TcpCommonServer(serverSocketProperties, serverSessionManage());
        server.setDispatch(requestHandle)
                .setMethodSupport(actionSupport)
                .setConvertManager(requestConvertManager)
                .setArgumentValues(argumentValues);

        server.addProcessor(Constant.REQUEST_SIGN, gatewayProcess);
        server.asyncStart();
        return server;
    }

    @Bean
    public GameTcpClient client() {
        GatewayClient client = new GatewayClient(new GameSocketProperties(), properties, clientSessionMange());
        FutureResponseProcess process = new FutureResponseProcess();
        client.addProcessor(Constant.RESPONSE_SIGN, process);
        client.start();
        return client;
    }

    @Bean
    public ForwardGatewayProcess gatewayProcess(GameTcpClient client) {
        ForwardSessionSelect select = new RandomSessionSelect();
        return new ForwardGatewayProcess(client, select, client.getService());
    }

    public SessionManage<Session> clientSessionMange() {
        SessionFactory<GameSession> factory = GameSession::of;
        return new GatewaySessionManage(factory);
    }

    protected SessionManage<GameSession> serverSessionManage() {
        SessionFactory<GameSession> sessionFactory = GameSession::of;
        return new GameSessionManage<>(sessionFactory, new GameSessionMemoryCacheManage<>());
    }
}
