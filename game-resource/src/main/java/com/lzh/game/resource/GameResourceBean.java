package com.lzh.game.resource;

import com.lzh.game.resource.data.ConcurrentResourceManageHandler;
import com.lzh.game.resource.data.DefaultResourceModelFactory;
import com.lzh.game.resource.data.ResourceManageHandler;
import com.lzh.game.resource.data.ResourceModelFactory;
import com.lzh.game.resource.data.mongo.MongoResourceManageHandler;
import com.lzh.game.resource.reload.ResourceReloadMange;
import com.lzh.game.resource.reload.ResourceReloadMangeImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties(GameResourceProperties.class)
public class GameResourceBean {

    @Bean
    public ResourceModelFactory resourceModelManage(GameResourceProperties resourceProperties) {
        return new DefaultResourceModelFactory(resourceProperties);
    }

    @Bean
    @ConditionalOnMissingClass
    public ResourceManageHandler resourceManageHandler(ResourceModelFactory resourceModelFactory, MongoTemplate mongoTemplate) {
        MongoResourceManageHandler handler = new MongoResourceManageHandler(resourceModelFactory, mongoTemplate, reloadMange());
        ConcurrentResourceManageHandler concurrent = new ConcurrentResourceManageHandler(resourceModelFactory, handler);
        concurrent.reload();
        return concurrent;
    }

    @Bean
    public ResourceReloadMange reloadMange() {
        return new ResourceReloadMangeImpl();
    }
}
