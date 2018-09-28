package com.lzh.netty.start.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import static java.util.Collections.singletonMap;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@Configuration
public class RedisConfig {
    //缓存管理器
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(1))
                .disableCachingNullValues();

        RedisCacheManager cm = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                //.initialCacheConfigurations(singletonMap("predefined", defaultCacheConfig().disableCachingNullValues()))
                .transactionAware()
                .build();
        return cm;
    }

    @Bean
    public ReactiveRedisTemplate reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {

        FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(getClass());
        ReactiveRedisTemplate template = new ReactiveRedisTemplate(reactiveRedisConnectionFactory
                ,RedisSerializationContext.fromSerializer(serializer));

        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        return template;
    }
    @Bean
    public FastJsonRedisSerializer fastJsonRedisSerializer() {
        return new FastJsonRedisSerializer(getClass());
    }

    /**
     * use redis to manage session
     * @deprecated keep alive connection isn't stateless, so it not appropriate to use cache
     * @param reactiveRedisTemplate
     * @return
     */
    /*@Bean
    public RedisSessionCache<GameSession> redisSessionCache(ReactiveRedisTemplate reactiveRedisTemplate) {
        RedisSessionCache<GameSession> sessionCache = new RedisSessionCache<>();
        sessionCache.setTemplate(reactiveRedisTemplate);
        return sessionCache;
    }*/
}
