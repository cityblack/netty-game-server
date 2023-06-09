package com.lzh.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.ActionMethodSupport;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.RequestHandle;
import com.lzh.game.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.socket.core.invoke.ConvertManager;
import com.lzh.game.socket.core.invoke.InvokeMethodArgumentValues;
import com.lzh.game.socket.core.session.*;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(GameSocketConfiguration.class)
public class TcpServerConfiguration {

    @Bean
    public GameServer gameServer(SpringGameServerProperties serverSocketProperties
            , RequestHandle requestHandle
            , ActionMethodSupport<EnhanceHandlerMethod> actionSupport
            , ConvertManager convertManager
            , InvokeMethodArgumentValues<Request> argumentValues) {

        TcpCommonServer server = new SpringTcpCommonServer(serverSocketProperties, serverSessionManage());
        server.setHandler(requestHandle)
                .setMethodSupport(actionSupport)
                .setConvertManager(convertManager)
                .setArgumentValues(argumentValues);
        server.asyncStart();
        return server;
    }

    protected SessionManage<GameSession> serverSessionManage() {
        SessionFactory<GameSession> sessionFactory = GameSession::of;
        return new GameSessionManage<>(sessionFactory, new GameSessionMemoryCacheManage<>());
    }
}
