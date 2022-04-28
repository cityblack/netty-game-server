package com.lzh.game.client;

import com.lzh.game.client.bootstrap.ClientGameSession;
import com.lzh.game.client.bootstrap.ClientMessageHandler;
import com.lzh.game.client.bootstrap.ResponseDispatcher;
import com.lzh.game.client.bootstrap.GameClientBootstrap;
import com.lzh.game.client.bootstrap.TcpClient;
import com.lzh.game.client.support.ActionMethodSupportImpl;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.common.scoket.ActionMethodSupport;
import com.lzh.game.common.scoket.GameSocketProperties;
import com.lzh.game.common.scoket.MessageHandler;
import com.lzh.game.common.scoket.session.GameSessionManage;
import com.lzh.game.common.scoket.session.SessionFactory;
import com.lzh.game.common.scoket.session.SessionManage;
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
    public MessageHandler clientMessage(ResponseDispatcher dispatcher) {
        return new ClientMessageHandler(dispatcher);
    }

    @Bean
    public TcpClient tcpClient(MessageHandler clientMessage, SessionManage<ClientGameSession> sessionManage) {
        return new GameClientBootstrap(clientMessage, sessionManage, properties);
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
