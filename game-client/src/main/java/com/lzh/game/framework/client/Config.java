package com.lzh.game.framework.client;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.framework.client.bootstrap.ResponseDispatcher;
import com.lzh.game.framework.client.bootstrap.ResponseProcess;
import com.lzh.game.socket.GameSocketProperties;
import com.lzh.game.socket.core.bootstrap.GameClient;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.invoke.InvokeSupport;
import com.lzh.game.socket.core.protocol.Request;
import com.lzh.game.socket.core.session.GameSessionManage;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionFactory;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.socket.core.session.impl.GameSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClientProperties.class)
public class Config {

    @Autowired
    private GameSocketProperties properties;

//    @Bean
//    public InvokeSupport<HandlerMethod> methodSupport() {
//        return ();
//    }

    @Bean
    public ResponseDispatcher responseDispatcher(InvokeSupport<HandlerMethod> methodSupport) {
        ResponseDispatcher dispatcher = new ResponseDispatcher(methodSupport);
        return dispatcher;
    }

    @Bean
    public GameClient tcpClient(ResponseProcess responseProcess) {
        SessionFactory<Session> sessionFactory = GameSession::of;
        GameSessionManage<Session> manage = new GameSessionManage<>(sessionFactory, new GameSessionMemoryCacheManage());
        GameTcpClient client = new GameTcpClient(properties, manage);
        client.addProcessor(Request.class, responseProcess);
        client.start();
        return client;
    }

    @Bean
    public ResponseProcess responseProcess(ResponseDispatcher dispatcher) {
        return new ResponseProcess(dispatcher);
    }
}
