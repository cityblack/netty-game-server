package com.lzh.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.process.RequestProcess;
import com.lzh.game.socket.core.RequestProcessPool;
import com.lzh.game.socket.core.ServerExchange;
import com.lzh.game.socket.core.session.*;
import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.invoke.*;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.socket.core.session.cache.SessionMemoryCacheManage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public ConvertManager convertManager() {
        return new DefaultConvertManager();
    }

    @Bean
    public RequestActionSupport<EnhanceHandlerMethod> actionSupport() {
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
    public RequestHandler requestHandler(RequestActionSupport<EnhanceHandlerMethod> actionMethodSupport
            , InvokeMethodArgumentValues<Request> invokeMethodArgumentValues
            , SpringExceptionHandler errorHandler, SpringInterceptorHandler interceptorHandler) {
        return new SpringRequestHandler(actionMethodSupport, invokeMethodArgumentValues, errorHandler, interceptorHandler);
    }

    @Bean
    public InvokeMethodArgumentValues invokeMethodArgumentValues(ConvertManager convertManager) {
        return new InvokeMethodArgumentValuesImpl(convertManager);
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
}
