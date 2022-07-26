package com.lzh.game.resource.data.load;

import com.lzh.game.resource.data.LoadResourceHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Slf4j
public class MongoLoadResourceHandler implements LoadResourceHandle {

    private MongoTemplate mongoTemplate;

    public MongoLoadResourceHandler(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<?> loadList(Class<?> type, String resourceName) {
        if (log.isDebugEnabled()) {
            log.debug("Loading {} from mongodb..", resourceName);
        }
        return mongoTemplate.findAll(type, resourceName);
    }
}
