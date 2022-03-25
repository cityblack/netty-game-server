package com.lzh.game.start;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.invoke.InnerParamDataBindHandler;
import com.lzh.game.start.filter.GmFilter;
import com.lzh.game.start.filter.ProtocolVersionFilter;
import com.lzh.game.start.model.core.ConsumeInnerParamDataBindHandler;
import com.lzh.game.start.pool.DefaultBusinessThreadPool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(StartProperties.class)
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

    /**
     * Use caffeine cache
     * @return
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterAccess(12, TimeUnit.HOURS)
                .initialCapacity(500)
                .maximumSize(3000));
        return cacheManager;
    }
}
