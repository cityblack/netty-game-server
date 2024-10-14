package com.lzh.game.framework.socket.starter.config;

import com.lzh.game.framework.socket.core.invoke.convert.DefaultInvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.support.DefaultActionInvokeSupport;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.protocol.message.DefaultMessageManager;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.DefaultSessionManage;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.starter.bean.SpringExceptionHandler;
import com.lzh.game.framework.socket.starter.bean.SpringInterceptorHandler;
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

    @Bean
    @ConditionalOnMissingBean
    public InvokeSupport invokeSupport() {
        return new DefaultActionInvokeSupport();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringExceptionHandler errorHandler() {
        return new SpringExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringInterceptorHandler interceptorHandler() {
        return new SpringInterceptorHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultInvokeMethodArgumentValues argumentValues() {
        return new DefaultInvokeMethodArgumentValues();
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionManage<Session> sessionManage() {
        return new DefaultSessionManage<>();
    }

    @Bean
    public MessageManager messageManager(SpringGameServerProperties serverProperties) {
        return new DefaultMessageManager(serverProperties);
    }

}
