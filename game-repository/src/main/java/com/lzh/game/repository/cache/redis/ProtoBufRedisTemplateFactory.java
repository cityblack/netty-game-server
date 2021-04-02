package com.lzh.game.repository.cache.redis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Use google proto buff the serializer value
 */
public class ProtoBufRedisTemplateFactory implements RedisTemplateFactory, ApplicationContextAware {

    private Map<Class<?>, RedisTemplate<String, ?>> templateFactoryMap = new ConcurrentHashMap<>();

    private ApplicationContext context;

    private RedisConnectionFactory factory;

    public ProtoBufRedisTemplateFactory(RedisConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Override
    public <T> RedisTemplate<String, T> getTemplate(Class<T> type) {
        if (!templateFactoryMap.containsKey(type)) {
            RedisTemplate template = createRedisTemplate(type);
            templateFactoryMap.put(type, template);
        }
        return (RedisTemplate<String, T>) templateFactoryMap.get(type);
    }

    private <T>RedisTemplate<String, T> createRedisTemplate(Class<T> type) {
        ProtoBufRedisTemplate redisTemplate = (ProtoBufRedisTemplate) context.getAutowireCapableBeanFactory().createBean(ProtoBufRedisTemplate.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
        redisTemplate.setType(type);
        context.getAutowireCapableBeanFactory().autowireBean(redisTemplate);
        return redisTemplate;
    }

    class ProtoBufRedisTemplate<T> extends RedisTemplate<String, T> {

        public ProtoBufRedisTemplate() {
            this.setConnectionFactory(factory);
        }

        private Class<T> type;

        @Override
        public RedisSerializer<?> getKeySerializer() {
            return new StringRedisSerializer();
        }

        @Override
        public RedisSerializer<T> getValueSerializer() {
            return new ProtoBufRedisSerializer<>(type);
        }

        @Override
        public RedisSerializer<?> getHashKeySerializer() {
            return new StringRedisSerializer();
        }

        @Override
        public RedisSerializer<?> getHashValueSerializer() {
            return new ProtoBufRedisSerializer<>(type);
        }

        public Class<T> getType() {
            return type;
        }

        public void setType(Class<T> type) {
            this.type = type;
        }
    }


}
