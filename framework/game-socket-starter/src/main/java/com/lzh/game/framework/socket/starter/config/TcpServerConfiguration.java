package com.lzh.game.framework.socket.starter.config;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServer;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.starter.server.SpringTcpCommonServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(GameSocketConfiguration.class)
public class TcpServerConfiguration {

    @Bean
    public GameServer gameServer(SpringGameServerProperties serverSocketProperties
            , MessageManager messageManager
            , SessionManage<Session> sessionManage
            , InvokeSupport invokeSupport) {
        var context = BootstrapContext.of(sessionManage, messageManager, invokeSupport);
        var server = new SpringTcpCommonServer(serverSocketProperties, context);
        server.asyncStart();
        return server;
    }
}
