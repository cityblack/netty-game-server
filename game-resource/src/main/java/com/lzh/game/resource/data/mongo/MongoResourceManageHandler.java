package com.lzh.game.resource.data.mongo;

import com.lzh.game.resource.data.DefaultResourceManageHandler;
import com.lzh.game.resource.data.ResourceModelFactory;
import com.lzh.game.resource.reload.ResourceReloadMange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Slf4j
public class MongoResourceManageHandler extends DefaultResourceManageHandler {

    private MongoTemplate mongoTemplate;

    public MongoResourceManageHandler(ResourceModelFactory resourceModels, MongoTemplate mongoTemplate, ResourceReloadMange reloadMange) {
        super(reloadMange, resourceModels);
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public <T> List<T> getData(Class<T> type, String resourceName) {
        if (log.isDebugEnabled()) {
            log.debug("Loading {} from mongodb..", resourceName);
        }
        return mongoTemplate.findAll(type, resourceName);
    }

}
