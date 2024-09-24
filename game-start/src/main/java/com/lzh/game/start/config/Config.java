package com.lzh.game.start.config;

import com.lzh.game.framework.socket.core.filter.Filter;
import com.lzh.game.start.StartProperties;
import com.lzh.game.start.filter.GmFilter;
import com.lzh.game.start.pool.DefaultBusinessThreadExecutorService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StartProperties.class)
public class Config {

    @Bean
    public Filter gmFilter() {
        return new GmFilter();
    }

    @Bean(name = "requestBusinessPool")
    public DefaultBusinessThreadExecutorService exchangeProcess() {
        return DefaultBusinessThreadExecutorService.getInstance();
    }

}
