package com.lzh.game.framework.socket.starter;

import com.lzh.game.framework.socket.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.invoke.RequestDispatch;
import com.lzh.game.framework.socket.core.invoke.convert.DefaultInvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.convert.InvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.support.DefaultActionInvokeSupport;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
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
    public InvokeSupport actionSupport() {
        return new SpringActionSupport(new DefaultActionInvokeSupport());
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
    public RequestDispatch requestHandler(InvokeSupport actionMethodSupport
            , InvokeMethodArgumentValues invokeMethodArgumentValues
            , SpringExceptionHandler errorHandler, SpringInterceptorHandler interceptorHandler) {
        return new SpringRequestHandler(actionMethodSupport, invokeMethodArgumentValues, errorHandler, interceptorHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public InvokeMethodArgumentValues invokeMethodArgumentValues() {
        return new DefaultInvokeMethodArgumentValues();
    }

    protected GameServerSocketProperties getServerSocketProperties() {
        return this.serverSocketProperties;
    }
}
