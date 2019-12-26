package com.lzh.game.start;

import com.lzh.game.socket.dispatcher.action.convent.InnerParamDataBindHandler;
import com.lzh.game.socket.dispatcher.filter.Filter;
import com.lzh.game.socket.dispatcher.filter.FilterConfig;
import com.lzh.game.socket.dispatcher.filter.FilterConfigManage;
import com.lzh.game.start.filter.GmFilter;
import com.lzh.game.start.filter.IPFilter;
import com.lzh.game.start.filter.ProtocolVersionFilter;
import com.lzh.game.start.model.core.ConsumeInnerParamDataBindHandler;
import com.lzh.game.start.model.world.scene.SceneManage;
import com.lzh.game.start.pool.DefaultBusinessThreadPool;
import com.lzh.game.start.server.GameGlobalServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public GameGlobalServer gameGlobalServer() {
        return new GameGlobalServer();
    }

    @Bean
    public InnerParamDataBindHandler innerParamDataBindHandler() {
        return new ConsumeInnerParamDataBindHandler();
    }

    @Bean
    public FilterConfigManage filterConfigManage(FilterConfig filterConfig) {
        //filterConfig.addAnonProtocol(10086);
        filterConfig.addFilter(new IPFilter());
        filterConfig.addFilter(protocolVersionFilter());
        filterConfig.addFilter(gmFilter());
        return () -> filterConfig;
    }

    @Bean
    public Filter protocolVersionFilter() {
        return new ProtocolVersionFilter();
    }

    @Bean
    public Filter gmFilter() {
        return new GmFilter();
    }

    @Bean
    public DefaultBusinessThreadPool exchangeProcess(SceneManage sceneManage) {
        return new DefaultBusinessThreadPool(sceneManage);
    }
}
