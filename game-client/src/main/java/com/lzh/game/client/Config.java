package com.lzh.game.client;

import com.lzh.game.client.bootstrap.ResponseDispatcher;
import com.lzh.game.client.bootstrap.ResponseProcess;
import com.lzh.game.client.support.ActionMethodSupportImpl;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.ActionMethodSupport;
import com.lzh.game.socket.GameClient;
import com.lzh.game.socket.GameSocketProperties;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.session.GameSession;
import com.lzh.game.socket.core.session.GameSessionManage;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionFactory;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClientProperties.class)
public class Config {

    @Autowired
    private GameSocketProperties properties;

    @Bean
    public ActionMethodSupport<HandlerMethod> methodSupport() {
        return new ActionMethodSupportImpl();
    }

    @Bean
    public ResponseDispatcher responseDispatcher(ActionMethodSupport<HandlerMethod> methodSupport) {
        ResponseDispatcher dispatcher = new ResponseDispatcher(methodSupport);
        return dispatcher;
    }

    @Bean
    public GameClient tcpClient(ResponseProcess responseProcess) {
        SessionFactory<Session> sessionFactory = GameSession::of;
        GameSessionManage<Session> manage = new GameSessionManage<>(sessionFactory, new GameSessionMemoryCacheManage());
        GameTcpClient client = new GameTcpClient(properties, manage);
        client.addProcess(Constant.REQUEST_SIGN, responseProcess);
        client.start();
        return client;
    }

    @Bean
    public ResponseProcess responseProcess(ResponseDispatcher dispatcher) {
        return new ResponseProcess(dispatcher);
    }
}
