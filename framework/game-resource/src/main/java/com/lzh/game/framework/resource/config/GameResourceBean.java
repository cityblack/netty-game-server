package com.lzh.game.framework.resource.config;

import com.lzh.game.framework.resource.data.*;
import com.lzh.game.framework.resource.data.cache.MemoryResourceCacheFactory;
import com.lzh.game.framework.resource.data.cache.ResourceCacheFactory;
import com.lzh.game.framework.resource.reload.ResourceReloadMeta;
import com.lzh.game.framework.resource.reload.SpringResourceReloadMeta;
import com.lzh.game.framework.resource.data.load.MongoResourceLoadHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.format.support.FormattingConversionService;

@Configuration
@EnableConfigurationProperties(GameResourceProperties.class)
public class GameResourceBean {

    @Bean
    public ResourceMetaManager resourceModelManage(GameResourceProperties resourceProperties) {
        return new DefaultResourceModelFactory(resourceProperties);
    }

    /**
     * Use mongo load and local memory cache
     * @param resourceModelMeta
     * @param mongoTemplate
     * @return
     */
    @Bean
    @ConditionalOnMissingClass
    public ResourceManager resourceManageHandler(ResourceMetaManager resourceModelMeta, MongoTemplate mongoTemplate) {
        MongoResourceLoadHandler loader = new MongoResourceLoadHandler(mongoTemplate);
        ResourceCacheFactory factory = new MemoryResourceCacheFactory();
        DefaultResourceManager handler = new DefaultResourceManager(loader, factory, reloadMeta(), resourceModelMeta);
        ConcurrentResourceManageHandler concurrent = new ConcurrentResourceManageHandler(resourceModelMeta, handler);
        concurrent.reload();
        return concurrent;
    }

    @Bean
    public ResourceReloadMeta reloadMeta() {
        return new SpringResourceReloadMeta();
    }

    @ConditionalOnMissingBean
    @Bean
    public ConversionService conversionService() {
        return new FormattingConversionService();
    }
}
