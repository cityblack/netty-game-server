package com.lzh.netty.socket.autoconfig;

import com.lzh.netty.socket.GameServer;
import com.lzh.netty.socket.dispatcher.*;
import com.lzh.netty.socket.dispatcher.action.ActionCenter;
import com.lzh.netty.socket.dispatcher.action.ActionCenterHandler;
import com.lzh.netty.socket.dispatcher.action.ActionSupport;
import com.lzh.netty.socket.dispatcher.action.DefaultActionSupport;
import com.lzh.netty.socket.dispatcher.filter.DefaultFilterConfig;
import com.lzh.netty.socket.dispatcher.filter.FilterConfig;
import com.lzh.netty.socket.dispatcher.protocol.DefaultProtocolManage;
import com.lzh.netty.socket.dispatcher.protocol.ProtocolManage;
import com.lzh.netty.socket.protocol.session.ChannelSession;
import com.lzh.netty.socket.protocol.session.GameSession;
import com.lzh.netty.socket.protocol.session.SessionManage;
import com.lzh.netty.socket.protocol.session.cache.DefaultSessionMemoryCache;
import com.lzh.netty.socket.protocol.session.cache.GameSessionMemoryCacheManage;
import com.lzh.netty.socket.protocol.session.cache.SessionMemoryCache;
import com.lzh.netty.socket.protocol.session.cache.SessionMemoryCacheManage;
import com.lzh.netty.socket.protocol.session.manage.GameSessionManage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(GameProperties.class)
public class GameAutoConfiguration {

    @Resource
    private GameProperties gameProperties;

    @Bean
    public TaskFactory taskFactory() {
        return new TaskManage();
    }

    @Bean
    public DispatcherHandler dispatcherHandler(ActionCenter actionCenter, RequestComposeHandler requestComposeHandler) {
        DispatcherHandler handler = new DispatcherHandler();
        handler.setResponseTime(gameProperties.getResponseTime());
        handler.setActionCenter(actionCenter);
        handler.setFactory(taskFactory());
        handler.setRequestComposeHandler(requestComposeHandler);
        return handler;
    }

    @Bean
    public ActionCenter actionCenter(ActionSupport actionSupport) {
        ActionCenterHandler center = new ActionCenterHandler();
        center.setSupport(actionSupport);
        return center;
    }

    @Bean
    public ActionSupport actionSupport(ProtocolManage protocolManage) {
        DefaultActionSupport support = new DefaultActionSupport();
        support.setProtocolManage(protocolManage);
        return support;
    }

    @Bean
    public GameServer gameServer(DispatcherHandler handler, ChannelSession channelSession) {
        GameServer gameServer = new GameServer(handler,channelSession, gameProperties.getPort());
        return gameServer;
    }

    @Bean
    public ChannelSession channelSession(SessionMemoryCacheManage<GameSession> sessionMemoryCacheManage) {
        return gameSessionManage(sessionMemoryCacheManage);
    }

    @Bean
    public SessionManage sessionManage(SessionMemoryCacheManage<GameSession> sessionMemoryCacheManage) {
        return gameSessionManage(sessionMemoryCacheManage);
    }

    @Bean
    protected GameSessionManage gameSessionManage(SessionMemoryCacheManage<GameSession> sessionMemoryCacheManage) {
        GameSessionManage manage = new GameSessionManage();
        manage.setCacheManage(sessionMemoryCacheManage);
        return manage;
    }

    @Bean
    public SessionMemoryCacheManage<GameSession> sessionMemoryCacheManage(SessionMemoryCache cache) {
        GameSessionMemoryCacheManage<GameSession> manage = new GameSessionMemoryCacheManage();
        manage.setCache(cache);
        return manage;
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionMemoryCache<String,GameSession> defaultSessionMemoryCache() {
        SessionMemoryCache<String,GameSession> memoryCache = new DefaultSessionMemoryCache<>();
        return memoryCache;
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestComposeHandler requestComposeHandler() {
        DefaultRequestComposeHandler handler = new DefaultRequestComposeHandler();
        return handler;
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterConfig filterConfig() {
        return new DefaultFilterConfig();
    }

    @Bean
    public ProtocolManage protocolManage() {
        return new DefaultProtocolManage();
    }
}
