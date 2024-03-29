package com.lzh.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.ActionMethodSupport;
import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.core.RequestHandle;
import com.lzh.game.socket.core.invoke.*;
import com.lzh.game.socket.core.invoke.convert.DefaultConvertManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Default config
 */
@Configuration
@EnableConfigurationProperties(SpringGameServerProperties.class)
public class GameSocketConfiguration {

    @Resource
    private SpringGameServerProperties serverSocketProperties;

    @Bean
    @ConditionalOnMissingBean
    public RequestConvertManager convertManager() {
        return new DefaultConvertManager();
    }

    @Bean
    public ActionMethodSupport<EnhanceHandlerMethod> actionSupport() {
        DefaultActionMethodSupport support = new DefaultActionMethodSupport();
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
    public RequestHandle requestHandler(ActionMethodSupport<EnhanceHandlerMethod> actionMethodSupport
            , InvokeMethodArgumentValues invokeMethodArgumentValues
            , SpringExceptionHandler errorHandler, SpringInterceptorHandler interceptorHandler) {
        return new SpringRequestHandler(actionMethodSupport, invokeMethodArgumentValues, errorHandler, interceptorHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public InvokeMethodArgumentValues invokeMethodArgumentValues(RequestConvertManager requestConvertManager) {
        return new InvokeMethodArgumentValuesImpl(requestConvertManager);
    }

    protected GameServerSocketProperties getServerSocketProperties() {
        return this.serverSocketProperties;
    }
}
