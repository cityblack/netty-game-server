package com.lzh.game.client;

import com.lzh.game.client.bootstrap.*;
import com.lzh.game.client.support.ActionMethodSupportImpl;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.ActionMethodSupport;
import com.lzh.game.socket.GameClient;
import com.lzh.game.socket.GameSocketProperties;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.session.*;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.socket.core.session.cache.SessionMemoryCacheManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    public GameClient tcpClient(SessionManage<Session> clientSessionManage, ResponseProcess responseProcess) {
        GameTcpClient client = new GameTcpClient(properties, clientSessionManage);
        client.addProcess(Constant.RESPONSE_COMMAND_KEY, responseProcess);
        client.start();
        return client;
    }

    @Bean
    public ResponseProcess responseProcess(ResponseDispatcher dispatcher) {
        return new ResponseProcess(dispatcher);
    }

    @Configuration
    @ConditionalOnMissingBean(name = "clientSessionManage")
    class SessionConfig {

        @Bean(name = "clientSessionManage")
        protected SessionManage<Session> clientSessionManage(SessionMemoryCacheManage<String, Session> clientCacheManager) {
            SessionFactory<Session> sessionFactory = GameSession::of;
            return new GameSessionManage<>(clientCacheManager, sessionFactory);
        }

        @Bean(name = "clientCacheManager")
        public SessionMemoryCacheManage<String, Session> clientCacheManager() {
            return new GameSessionMemoryCacheManage();
        }
    }
}
