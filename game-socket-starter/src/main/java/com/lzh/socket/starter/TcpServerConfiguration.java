package com.lzh.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.socket.core.invoke.ConvertManager;
import com.lzh.game.socket.core.invoke.InvokeMethodArgumentValues;
import com.lzh.game.socket.core.invoke.RequestActionSupport;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

@Configuration
@Import(GameSocketConfiguration.class)
public class TcpServerConfiguration {

    @Resource
    private SessionManage<Session> sessionManage;

    @Bean
    public GameServer gameServer(SpringGameServerProperties serverSocketProperties
            , RequestHandler requestHandler
            , RequestActionSupport<EnhanceHandlerMethod> actionSupport
            , ConvertManager convertManager
            , InvokeMethodArgumentValues<Request> argumentValues) {

        TcpCommonServer server = new TcpCommonServer(serverSocketProperties, sessionManage);
        server.setHandler(requestHandler)
                .setMethodSupport(actionSupport)
                .setConvertManager(convertManager)
                .setArgumentValues(argumentValues);
        server.asyncStart();
        return server;
    }
}
