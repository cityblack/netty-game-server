package com.lzh.socket.starter;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.RequestProcess;
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
            , RequestActionSupport actionSupport
            , ConvertManager convertManager
            , InvokeMethodArgumentValues argumentValues
            , RequestProcess requestProcess) {

        TcpCommonServer server = new TcpCommonServer(serverSocketProperties, sessionManage);
        server.setHandler(requestHandler)
                .setMethodSupport(actionSupport)
                .setConvertManager(convertManager)
                .setArgumentValues(argumentValues);
        server.addProcess(Constant.REQUEST_COMMAND_KEY, requestProcess);
        server.asyncStart();
        return server;
    }
}
