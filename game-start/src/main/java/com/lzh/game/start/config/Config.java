package com.lzh.game.start.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.lzh.game.framework.socket.core.filter.Filter;
import com.lzh.game.framework.socket.core.invoke.convert.RequestConvertManager;
import com.lzh.game.socket.core.invoke.convert.DefaultConvertManager;
import com.lzh.game.start.StartProperties;
import com.lzh.game.start.filter.GmFilter;
import com.lzh.game.start.filter.ProtocolVersionFilter;
import com.lzh.game.start.pool.DefaultBusinessThreadExecutorService;
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
    public Filter protocolVersionFilter() {
        return new ProtocolVersionFilter();
    }

    @Bean
    public Filter gmFilter() {
        return new GmFilter();
    }

    @Bean(name = "requestBusinessPool")
    public DefaultBusinessThreadExecutorService exchangeProcess() {
        return DefaultBusinessThreadExecutorService.getInstance();
    }

    @Bean
    public RequestConvertManager convertManager() {
        return new ConsumeConvertManager(new DefaultConvertManager());
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

    @Bean
    public SchedulerOption schedulerOption(Scheduler scheduler) {
        return new SchedulerOptionImpl(scheduler);
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
