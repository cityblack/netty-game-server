package com.lzh.game.socket.config;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.invoke.*;
import com.lzh.game.socket.core.invoke.cmd.CmdMappingManage;
import com.lzh.game.socket.core.invoke.cmd.CmdParseFactory;
import com.lzh.game.socket.core.invoke.cmd.DefaultCmdMappingManage;
import com.lzh.game.socket.core.session.ServerGameSession;
import com.lzh.game.common.scoket.session.GameSessionManage;
import com.lzh.game.common.scoket.session.SessionFactory;
import com.lzh.game.common.scoket.session.SessionManage;
import com.lzh.game.common.scoket.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.common.scoket.session.cache.SessionMemoryCacheManage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Collections;

@Configuration
@EnableConfigurationProperties(GameServerSocketProperties.class)
public class GameSocketConfiguration {

    @Resource
    private GameServerSocketProperties serverSocketProperties;

    @Bean
    public RequestActionSupport<EnhanceHandlerMethod> actionSupport(CmdMappingManage cmdMappingManage, InnerParamDataBindHandler innerParamDataBindHandler) {
        DefaultActionMethodSupport support = new DefaultActionMethodSupport();
        support.setCmdMappingManage(cmdMappingManage);
        support.setInnerParamDataBindHandler(innerParamDataBindHandler);
        return support;
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
        protected SessionManage sessionManage(SessionMemoryCacheManage<String, ServerGameSession> sessionMemoryCacheManage) {
            SessionFactory<ServerGameSession> sessionFactory = ServerGameSession::of;
            return new GameSessionManage(sessionMemoryCacheManage, sessionFactory);
        }

        @Bean
        public SessionMemoryCacheManage<String, ServerGameSession> sessionMemoryCacheManage() {
            return new GameSessionMemoryCacheManage();
        }
    }

    @Bean
    public CmdMappingManage cmdMappingManage(CmdParseFactory cmdParseFactory) {
        return new DefaultCmdMappingManage(cmdParseFactory);
    }

    @Bean
    @ConditionalOnMissingClass
    @ConditionalOnMissingBean
    public CmdParseFactory parseCmdLoad() {
        return () -> Collections.EMPTY_LIST;
    }
}
