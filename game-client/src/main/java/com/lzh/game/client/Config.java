package com.lzh.game.client;

import com.lzh.game.client.bootstrap.*;
import com.lzh.game.client.support.ActionMethodSupportImpl;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.ActionMethodSupport;
import com.lzh.game.socket.GameSocketProperties;
import com.lzh.game.socket.core.session.GameSessionManage;
import com.lzh.game.socket.core.session.SessionFactory;
import com.lzh.game.socket.core.session.SessionManage;
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
    public TcpClient tcpClient(SessionManage<ClientGameSession> sessionManage, ResponseProcess responseProcess) {
        GameClientBootstrap client = new GameClientBootstrap(sessionManage, properties);
        client.addProcess(Constant.RESPONSE_COMMAND_KEY, responseProcess);
        client.start();
        return client;
    }

    @Bean
    public ResponseProcess responseProcess(ResponseDispatcher dispatcher) {
        return new ResponseProcess(dispatcher);
    }

    @Configuration
    @ConditionalOnMissingBean(value = SessionManage.class)
    class SessionConfig {

        @Bean
        protected SessionManage<ClientGameSession> sessionManage(SessionMemoryCacheManage<String, ClientGameSession> sessionMemoryCacheManage) {
            SessionFactory<ClientGameSession> sessionFactory = ClientGameSession::of;
            return new GameSessionManage<>(sessionMemoryCacheManage, sessionFactory);
        }

        @Bean
        public SessionMemoryCacheManage<String, ClientGameSession> sessionMemoryCacheManage() {
            return new GameSessionMemoryCacheManage();
        }
    }
}
