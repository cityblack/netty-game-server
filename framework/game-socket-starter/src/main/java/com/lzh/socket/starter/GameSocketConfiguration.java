package com.lzh.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.core.invoke.*;
import com.lzh.game.socket.core.invoke.convert.DefaultConvertManager;
import com.lzh.game.socket.core.process.RequestDispatch;
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
    public RequestConvertManager convertManager() {
        return new DefaultConvertManager();
    }

    @Bean
    public InvokeSupport<EnhanceHandlerMethod> actionSupport() {
        DefaultActionInvokeSupport support = new DefaultActionInvokeSupport();
        return new SpringActionSupport(support, convertManager());
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
    public RequestDispatch requestHandler(InvokeSupport<EnhanceHandlerMethod> actionMethodSupport
            , InvokeMethodArgumentValues invokeMethodArgumentValues
            , SpringExceptionHandler errorHandler, SpringInterceptorHandler interceptorHandler) {
        return new SpringRequestHandler(actionMethodSupport, invokeMethodArgumentValues, errorHandler, interceptorHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public InvokeMethodArgumentValues invokeMethodArgumentValues() {
        return new InvokeMethodArgumentValuesImpl();
    }

    protected GameServerSocketProperties getServerSocketProperties() {
        return this.serverSocketProperties;
    }
}
