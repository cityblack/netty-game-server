package com.lzh.game.repository.db.redis;


import org.springframework.data.redis.core.RedisTemplate;

/**
 * RedisTemplate manage factory, Get the template from the factory
 * If use this factory. Must defined RedisTemplate bean's name
 */
public interface RedisTemplateFactory {

    <T>RedisTemplate<String, T> getTemplate(Class<T> type);
}
