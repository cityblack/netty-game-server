package com.lzh.game.framework.repository.config;

import com.lzh.game.framework.repository.cache.CacheFactory;
import com.lzh.game.framework.repository.persist.PersistRepository;
import com.lzh.game.framework.repository.inject.RepositoryInjectProcessor;
import com.lzh.game.framework.repository.cache.caffeine.CaffeineCacheFactory;
import com.lzh.game.framework.repository.persist.PersistFactory;
import com.lzh.game.framework.repository.persist.consumer.LocationPersistConsumer;
import com.lzh.game.framework.repository.persist.consumer.PersistConsumer;
import com.lzh.game.framework.repository.persist.db.MongoPersistRepository;
import com.lzh.game.framework.repository.persist.queue.LocationMemoryPersistFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties(RepositoryConfig.class)
public class RepositoryBean {

    @Bean
    @ConditionalOnMissingBean
    public PersistFactory persistFactory(PersistConsumer consumer, RepositoryConfig config) {
        return new LocationMemoryPersistFactory(config, consumer);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheFactory cacheFactory(RepositoryConfig config) {
        return new CaffeineCacheFactory(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public PersistConsumer persistConsumer(PersistRepository persistRepository) {
        return new LocationPersistConsumer(persistRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public PersistRepository persistRepository(MongoTemplate mongoTemplate) {
        return new MongoPersistRepository(mongoTemplate, mongoTemplate.getConverter().getMappingContext());
    }

}
