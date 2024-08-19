package com.lzh.game.framework.resource.data.load;

import com.lzh.game.framework.resource.data.ResourceLoadHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Slf4j
public class MongoResourceLoadHandler implements ResourceLoadHandle {

    private MongoTemplate mongoTemplate;

    public MongoResourceLoadHandler(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public <T>List<T> loadList(Class<T> type, String resourceName) {
        if (log.isDebugEnabled()) {
            log.debug("Loading {} from mongodb..", resourceName);
        }
        return mongoTemplate.findAll(type, resourceName);
    }
}
