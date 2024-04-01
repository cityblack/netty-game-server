package com.lzh.game.framework.repository;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzh.game.framework.repository.db.Persist;
import com.lzh.game.framework.repository.db.PersistRepository;
import com.lzh.game.framework.repository.db.persist.LocationMemoryPersistQueue;
import com.lzh.game.framework.repository.db.persist.LocationPersistConsumer;
import com.lzh.game.framework.repository.db.persist.PersistConsumer;
import com.lzh.game.framework.repository.db.persist.MongoPersistRepository;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableConfigurationProperties(GameRepositoryConfig.class)
public class GameRepositoryBean {

    @Bean
    public Persist persist(PersistConsumer consumer, GameRepositoryConfig config) {
        LocationMemoryPersistQueue persist = new LocationMemoryPersistQueue(consumer);
        persist.setConsumeIntervalTime(config.getConsumeIntervalTime());
        return persist;
    }

    @Bean
    public PersistConsumer persistConsumer(PersistRepository persistRepository) {
        return new LocationPersistConsumer(persistRepository);
    }

    @Bean
    public PersistRepository persistRepository(MongoTemplate mongoTemplate) {
        PersistRepository repository = new MongoPersistRepository(mongoTemplate, mongoTemplate.getConverter().getMappingContext());
        return repository;
    }

    @Bean
    public RepositoryInjectProcessor repositoryInjectProcessor() {
        return new RepositoryInjectProcessor();
    }

    @ConditionalOnMissingBean(CacheManager.class)
    class CacheConfig {

        @Bean
        public CacheManager cacheManager(RedissonClient redissonClient) {
            return new RedissonSpringCacheManager(redissonClient);
        }

        @Bean
        public RedisSerializer redisSerializer() {
            Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            return serializer;
        }
    }
}
