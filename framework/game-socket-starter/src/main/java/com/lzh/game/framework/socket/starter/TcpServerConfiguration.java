package com.lzh.game.framework.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.framework.socket.core.invoke.InvokeSupport;
import com.lzh.game.framework.socket.core.bootstrap.GameServer;
import com.lzh.game.framework.socket.core.process.RequestDispatch;
import com.lzh.game.framework.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.framework.socket.core.invoke.RequestConvertManager;
import com.lzh.game.framework.socket.core.invoke.InvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.session.GameSessionManage;
import com.lzh.game.framework.socket.core.session.SessionFactory;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.framework.socket.core.session.impl.GameSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(GameSocketConfiguration.class)
public class TcpServerConfiguration {

    @Bean
    public GameServer gameServer(SpringGameServerProperties serverSocketProperties
            , RequestDispatch requestHandle
            , InvokeSupport actionSupport
            , RequestConvertManager requestConvertManager
            , InvokeMethodArgumentValues<Request> argumentValues) {

        TcpCommonServer server = new SpringTcpCommonServer(serverSocketProperties, serverSessionManage());
        server.setDispatch(requestHandle)
                .setMethodSupport(actionSupport)
                .setConvertManager(requestConvertManager)
                .setArgumentValues(argumentValues);
        server.asyncStart();
        return server;
    }

    protected SessionManage<GameSession> serverSessionManage() {
        SessionFactory<GameSession> sessionFactory = GameSession::of;
        return new GameSessionManage<>(sessionFactory, new GameSessionMemoryCacheManage<>());
    }
}
