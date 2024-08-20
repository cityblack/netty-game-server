package com.lzh.game.framework.resource.data.load;

import com.lzh.game.framework.resource.data.meta.ResourceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Slf4j
public class MongoResourceLoadHandler implements ResourceLoadHandler {

    private MongoTemplate mongoTemplate;

    public MongoResourceLoadHandler(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public <T>List<T> loadList(ResourceMeta<T> meta) {
        if (log.isDebugEnabled()) {
            log.debug("Loading {} from mongodb..", meta.getResourceName());
        }
        return mongoTemplate.findAll(meta.getDataType(), meta.getResourceName());
    }
}
