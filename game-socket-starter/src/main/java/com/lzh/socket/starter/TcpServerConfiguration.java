package com.lzh.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.ActionMethodSupport;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.socket.core.invoke.ConvertManager;
import com.lzh.game.socket.core.invoke.InvokeMethodArgumentValues;
import com.lzh.game.socket.core.session.*;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

@Configuration
@Import(GameSocketConfiguration.class)
public class TcpServerConfiguration {

    @Bean
    public GameServer gameServer(SpringGameServerProperties serverSocketProperties
            , RequestHandler requestHandler
            , ActionMethodSupport<EnhanceHandlerMethod> actionSupport
            , ConvertManager convertManager
            , InvokeMethodArgumentValues<Request> argumentValues) {

        TcpCommonServer server = new TcpCommonServer(serverSocketProperties, serverSessionManage());
        server.setHandler(requestHandler)
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
