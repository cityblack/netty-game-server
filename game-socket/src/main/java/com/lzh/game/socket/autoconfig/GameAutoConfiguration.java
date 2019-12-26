package com.lzh.game.socket.autoconfig;

import com.lzh.game.socket.TcpGameServerBootstrap;
import com.lzh.game.socket.dispatcher.*;
import com.lzh.game.socket.dispatcher.action.ActionCenter;
import com.lzh.game.socket.dispatcher.action.ActionCenterHandler;
import com.lzh.game.socket.dispatcher.action.ActionMethodSupport;
import com.lzh.game.socket.dispatcher.action.DefaultActionMethodSupport;
import com.lzh.game.socket.dispatcher.action.convent.*;
import com.lzh.game.socket.dispatcher.filter.DefaultFilterConfig;
import com.lzh.game.socket.dispatcher.filter.FilterConfig;
import com.lzh.game.socket.dispatcher.mapping.CmdMappingManage;
import com.lzh.game.socket.exchange.handle.CommonChannelInitializer;
import com.lzh.game.socket.exchange.handle.WebSocketChannelInitializer;
import com.lzh.game.socket.exchange.session.ChannelSessionManage;
import com.lzh.game.socket.exchange.session.GameSession;
import com.lzh.game.socket.exchange.session.SessionManage;
import com.lzh.game.socket.exchange.session.cache.DefaultSessionMemoryCache;
import com.lzh.game.socket.exchange.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.socket.exchange.session.cache.SessionMemoryCache;
import com.lzh.game.socket.exchange.session.cache.SessionMemoryCacheManage;
import com.lzh.game.socket.exchange.session.manage.ChannelSessionManageAdapter;
import com.lzh.game.socket.exchange.session.manage.GameSessionManage;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(GameSocketProperties.class)
public class GameAutoConfiguration {

    @Resource
    private GameSocketProperties gameSocketProperties;

    @Bean
    public DispatcherHandler dispatcherHandler(ActionCenter actionCenter, RequestComposeHandler requestComposeHandler, ExchangeProcess exchangeProcess) {
        DispatcherHandler handler = new DispatcherHandler();
        handler.setActionCenter(actionCenter);
        handler.setProcess(exchangeProcess);
        handler.setRequestComposeHandler(requestComposeHandler);
        return handler;
    }

    @Bean
    public ActionCenter actionCenter(ActionMethodSupport actionMethodSupport, InvokeMethodArgumentValues invokeMethodArgumentValues) {
        ActionCenterHandler center = new ActionCenterHandler(actionMethodSupport, invokeMethodArgumentValues);
        return center;
    }

    @Bean
    public ActionMethodSupport actionSupport(CmdMappingManage cmdMappingManage, InnerParamDataBindHandler innerParamDataBindHandler) {
        DefaultActionMethodSupport support = new DefaultActionMethodSupport();
        support.setCmdMappingManage(cmdMappingManage);
        support.setInnerParamDataBindHandler(innerParamDataBindHandler);
        return support;
    }

    @Bean
    public TcpGameServerBootstrap gameServer(ChannelInitializer<SocketChannel> channelInitializer) {
        TcpGameServerBootstrap tcpGameServerBootstrap = new TcpGameServerBootstrap(gameSocketProperties.getPort(), channelInitializer);
        return tcpGameServerBootstrap;
    }

    /**
     * WebSocket channel or commonChannel
     * {@link WebSocketChannelInitializer}
     * {@link CommonChannelInitializer}
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ChannelInitializer<SocketChannel> channelInitializer() {
        return new WebSocketChannelInitializer();
    }

    @Bean
    public ChannelSessionManage channelSession(SessionMemoryCacheManage<GameSession> sessionMemoryCacheManage) {
        ChannelSessionManageAdapter adapter = new ChannelSessionManageAdapter();
        adapter.setGameSessionManage(gameSessionManage(sessionMemoryCacheManage));
        return adapter;
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
    public InvokeMethodArgumentValues invokeMethodArgumentValues(InnerParamDataBindHandler handler) {
        ProBufInvokeMethodArgumentValues values = new ProBufInvokeMethodArgumentValues(handler);
        return values;
    }
}
