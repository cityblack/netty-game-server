package com.lzh.game.framework.socket.starter.config;

import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.invoke.RequestDispatch;
import com.lzh.game.framework.socket.core.invoke.convert.DefaultInvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.support.DefaultActionInvokeSupport;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.protocol.message.DefaultMessageManager;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.GameSessionManage;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionFactory;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.framework.socket.core.session.impl.GameSession;
import com.lzh.game.framework.socket.starter.bean.SpringExceptionHandler;
import com.lzh.game.framework.socket.starter.bean.SpringInterceptorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Default config
 */
@Configuration
@EnableConfigurationProperties(SpringGameServerProperties.class)
public class GameSocketConfiguration {

    @Autowired
    private SpringGameServerProperties serverSocketProperties;

    @Bean
    @ConditionalOnMissingBean
    public InvokeSupport invokeSupport() {
        return new DefaultActionInvokeSupport();
    }

    @Bean
    public SpringExceptionHandler errorHandler() {
        return new SpringExceptionHandler();
    }

    @Bean
    public SpringInterceptorHandler interceptorHandler() {
        return new SpringInterceptorHandler();
    }

    @Bean
    public DefaultInvokeMethodArgumentValues argumentValues() {
        return new DefaultInvokeMethodArgumentValues();
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionManage<Session> sessionManage() {
        SessionFactory<Session> sessionFactory = GameSession::of;
        return new GameSessionManage<>(sessionFactory, new GameSessionMemoryCacheManage<>());
    }

    @Bean
    public MessageManager messageManager() {
        return new DefaultMessageManager();
    }

    protected GameServerSocketProperties getServerSocketProperties() {
        return this.serverSocketProperties;
    }
}
