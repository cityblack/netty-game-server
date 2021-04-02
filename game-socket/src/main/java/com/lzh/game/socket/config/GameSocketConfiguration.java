package com.lzh.game.socket.config;

import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.invoke.*;
import com.lzh.game.socket.core.invoke.cmd.CmdMappingManage;
import com.lzh.game.socket.core.invoke.cmd.CmdParseFactory;
import com.lzh.game.socket.core.invoke.cmd.DefaultCmdMappingManage;
import com.lzh.game.socket.core.session.DefaultGameSession;
import com.lzh.game.socket.core.session.GameSessionManage;
import com.lzh.game.socket.core.session.SessionFactory;
import com.lzh.game.socket.core.session.SessionManage;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.socket.core.session.cache.SessionMemoryCacheManage;
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
    public ActionMethodSupport actionSupport(CmdMappingManage cmdMappingManage, InnerParamDataBindHandler innerParamDataBindHandler) {
        DefaultActionMethodSupport support = new DefaultActionMethodSupport();
        support.setCmdMappingManage(cmdMappingManage);
        support.setInnerParamDataBindHandler(innerParamDataBindHandler);
        return support;
    }

    @Bean("requestHandler")
    public RequestHandler requestHandler(ActionMethodSupport actionMethodSupport, InvokeMethodArgumentValues invokeMethodArgumentValues) {
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
        protected SessionManage sessionManage(SessionMemoryCacheManage<String, DefaultGameSession> sessionMemoryCacheManage) {
            SessionFactory<DefaultGameSession> sessionFactory = channel -> new DefaultGameSession(channel);
            return new GameSessionManage(sessionMemoryCacheManage, sessionFactory);
        }

        @Bean
        public SessionMemoryCacheManage<String, DefaultGameSession> sessionMemoryCacheManage() {
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
