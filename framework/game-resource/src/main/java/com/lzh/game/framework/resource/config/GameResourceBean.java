package com.lzh.game.framework.resource.config;

import com.lzh.game.framework.resource.data.DefaultResourceMetaManager;
import com.lzh.game.framework.resource.data.load.MongoResourceLoadHandler;
import com.lzh.game.framework.resource.data.load.ResourceLoadHandler;
import com.lzh.game.framework.resource.data.meta.ResourceMetaManager;
import com.lzh.game.framework.resource.storage.manager.StorageManageFactory;
import com.lzh.game.framework.resource.storage.manager.StorageManager;
import com.lzh.game.framework.resource.reload.ResourceReloadHandler;
import com.lzh.game.framework.resource.reload.SpringResourceReloadHandler;
import com.lzh.game.framework.resource.storage.StorageFactory;
import com.lzh.game.framework.resource.storage.impl.CasStorageFactory;
import com.lzh.game.framework.resource.storage.impl.DefaultStorageFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties(GameResourceProperties.class)
public class GameResourceBean {

    @Bean
    public StorageManageFactory storageManager() {
        return new StorageManageFactory();
    }

    @Bean
    public ResourceMetaManager resourceModelManage(GameResourceProperties resourceProperties) {
        return new DefaultResourceMetaManager(resourceProperties);
    }

    @Bean
    public ResourceReloadHandler reloadMeta(StorageManager storageManager, ResourceMetaManager resourceModelManage) {
        return new SpringResourceReloadHandler(storageManager, resourceModelManage);
    }

    @Bean
    @ConditionalOnMissingBean
    public StorageFactory storageFactory(ResourceLoadHandler resourceLoadHandler) {
        return new CasStorageFactory(new DefaultStorageFactory(resourceLoadHandler));
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceLoadHandler resourceLoadHandler(MongoTemplate mongoTemplate) {
        return new MongoResourceLoadHandler(mongoTemplate);
    }
}
