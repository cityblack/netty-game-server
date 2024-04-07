package com.lzh.game.framework.gateway;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.ActionMethodSupport;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.GameSocketProperties;
import com.lzh.game.socket.core.ForwardSessionSelect;
import com.lzh.game.socket.core.RequestHandle;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.socket.core.invoke.RequestConvertManager;
import com.lzh.game.socket.core.invoke.InvokeMethodArgumentValues;
import com.lzh.game.socket.core.process.ForwardGatewayProcess;
import com.lzh.game.socket.core.process.FutureResponseProcess;
import com.lzh.game.socket.core.session.*;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.socket.starter.GameSocketConfiguration;
import com.lzh.socket.starter.SpringGameServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

@Configuration
@Import(GameSocketConfiguration.class)
@EnableConfigurationProperties(GatewayProperties.class)
public class GateConfiguration {

    @Resource
    private GatewayProperties properties;

    @Bean
    public GameServer gameServer(SpringGameServerProperties serverSocketProperties
            , RequestHandle requestHandle
            , ActionMethodSupport<EnhanceHandlerMethod> actionSupport
            , RequestConvertManager requestConvertManager
            , InvokeMethodArgumentValues<Request> argumentValues, ForwardGatewayProcess gatewayProcess) {

        TcpCommonServer server = new TcpCommonServer(serverSocketProperties, serverSessionManage());
        server.setHandler(requestHandle)
                .setMethodSupport(actionSupport)
                .setConvertManager(requestConvertManager)
                .setArgumentValues(argumentValues);

        server.addProcess(Constant.REQUEST_SIGN, gatewayProcess);
        server.asyncStart();
        return server;
    }

    @Bean
    public GameTcpClient client() {
        GatewayClient client = new GatewayClient(new GameSocketProperties(), properties, clientSessionMange());
        FutureResponseProcess process = new FutureResponseProcess();
        client.addProcess(Constant.RESPONSE_SIGN, process);
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
