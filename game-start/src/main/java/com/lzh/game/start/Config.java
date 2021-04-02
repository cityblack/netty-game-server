package com.lzh.game.start;

import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.invoke.InnerParamDataBindHandler;
import com.lzh.game.start.filter.GmFilter;
import com.lzh.game.start.filter.ProtocolVersionFilter;
import com.lzh.game.start.model.core.ConsumeInnerParamDataBindHandler;
import com.lzh.game.start.pool.DefaultBusinessThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public InnerParamDataBindHandler innerParamDataBindHandler() {
        return new ConsumeInnerParamDataBindHandler();
    }

    @Bean
    public Filter protocolVersionFilter() {
        return new ProtocolVersionFilter();
    }

    @Bean
    public Filter gmFilter() {
        return new GmFilter();
    }

    @Bean(name = "requestBusinessPool")
    public DefaultBusinessThreadPool exchangeProcess() {
        DefaultBusinessThreadPool pool = DefaultBusinessThreadPool.getInstance();
        return pool;
    }
}
