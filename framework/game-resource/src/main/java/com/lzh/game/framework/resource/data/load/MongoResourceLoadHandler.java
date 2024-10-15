package com.lzh.game.framework.resource.data.load;

import com.lzh.game.framework.resource.data.DictResource;
import com.lzh.game.framework.resource.data.meta.ResourceMeta;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class MongoResourceLoadHandler implements ResourceLoadHandler {

    private MongoTemplate mongoTemplate;

    public MongoResourceLoadHandler(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public <T> List<T> loadList(ResourceMeta<T> meta) {
        if (log.isDebugEnabled()) {
            log.debug("Loading {} from mongodb..", meta.getResourceName());
        }
        try (var cursor = mongoTemplate.getCollection(meta.getResourceName())
                .find()
                .iterator()) {
            int available = cursor.available();
            List<T> result = available > 0 ? new ArrayList<>(available) : new ArrayList<>();
            while (cursor.hasNext()) {
                Document object = cursor.next();
                var data = mongoTemplate.getConverter().read(meta.getDataType(), object);
                result.add(data);
                if (data instanceof DictResource dictResource) {
                    for (Map.Entry<String, Object> entry : object.entrySet()) {
                        dictResource.dict(entry.getKey(), entry.getValue());
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("loaded {} size: {}", meta.getResourceName(), result.size());
            }

            return Collections.unmodifiableList(result);
        }
    }
}
