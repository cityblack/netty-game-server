package com.lzh.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.MessageHandler;
import com.lzh.game.socket.core.session.GameSessionManage;
import com.lzh.game.socket.core.session.SessionFactory;
import com.lzh.game.socket.core.session.SessionManage;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.socket.core.session.cache.SessionMemoryCacheManage;
import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.invoke.*;
import com.lzh.game.socket.core.session.ServerGameSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Default config
 */
@Configuration
@EnableConfigurationProperties(GameServerSocketProperties.class)
public class GameSocketConfiguration {

    @Resource
    private GameServerSocketProperties serverSocketProperties;

    @Bean
    public RequestActionSupport<EnhanceHandlerMethod> actionSupport(InnerParamBindHandler innerParamBindHandler) {
        DefaultActionMethodSupport support = new DefaultActionMethodSupport();
        return new SpringActionSupport(support, innerParamBindHandler);
    }

    @Bean("requestHandler")
    public RequestHandler requestHandler(RequestActionSupport<EnhanceHandlerMethod> actionMethodSupport, InvokeMethodArgumentValues invokeMethodArgumentValues) {
        return new ActionRequestHandler(actionMethodSupport, invokeMethodArgumentValues);
    }

    @Bean
    public InvokeMethodArgumentValues invokeMethodArgumentValues() {
        return new InvokeMethodArgumentValuesImpl();
    }

    protected GameServerSocketProperties getServerSocketProperties() {
        return this.serverSocketProperties;
    }

    @Configuration
    @ConditionalOnMissingBean(value = SessionManage.class)
    class SessionConfig {

        @Bean
        protected SessionManage<ServerGameSession> sessionManage(SessionMemoryCacheManage<String, ServerGameSession> sessionMemoryCacheManage) {
            SessionFactory<ServerGameSession> sessionFactory = ServerGameSession::of;
            return new GameSessionManage<>(sessionMemoryCacheManage, sessionFactory);
        }

        @Bean
        public SessionMemoryCacheManage<String, ServerGameSession> sessionMemoryCacheManage() {
            return new GameSessionMemoryCacheManage<>();
        }
    }

    @Bean
    @ConditionalOnMissingBean(value = MessageHandler.class)
    public SocketHandlerBuilder builder() {
        return new SocketHandlerBuilder();
    }
}
