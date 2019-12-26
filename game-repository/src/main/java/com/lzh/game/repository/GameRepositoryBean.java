package com.lzh.game.repository;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzh.game.repository.db.Persist;
import com.lzh.game.repository.db.PersistRepository;
import com.lzh.game.repository.db.persist.*;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(GameRepositoryConfig.class)
public class GameRepositoryBean {

    /**
     * Use redis cache interface
     * @param persist
     * @return
     */
    @Bean
    public CacheDataRepository cacheDataRepository(Persist persist, CacheManager cacheManager, GameRepositoryConfig config) {

        CacheDataRepositoryImpl repository = new CacheDataRepositoryImpl(cacheManager, persist);
        repository.setClearMemAfterClose(config.isClearRedisAfterClose());
        return repository;
    }

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
    @ConditionalOnMissingBean
    public RedisSerializer fastJsonRedisSerializer() {
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        return serializer;
    }

    @Bean
    public PersistRepository persistRepository(MongoTemplate mongoTemplate) {
        PersistRepository repository = new MongoPersistRepository(mongoTemplate, mongoTemplate.getConverter().getMappingContext());
        return repository;
    }

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) throws IOException {
        return new RedissonSpringCacheManager(redissonClient);
    }
}
