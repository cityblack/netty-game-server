package com.lzh.game.framework.repository.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.lzh.game.framework.repository.cache.CacheFactory;
import com.lzh.game.framework.repository.cache.Cache;
import com.lzh.game.framework.repository.config.ComposeConfig;
import com.lzh.game.framework.repository.config.RepositoryConfig;

import java.util.concurrent.TimeUnit;

/**
 * @author zehong.l
 * @since 2024-10-12 15:03
 **/
public class CaffeineCacheFactory implements CacheFactory {

    private final RepositoryConfig config;

    public CaffeineCacheFactory(RepositoryConfig config) {
        this.config = config;
    }

    @Override
    public Cache createCache(String name, Class<?> type) {
        var compose = new ComposeConfig(config, type);
        var build = Caffeine.newBuilder()
                .initialCapacity(compose.getCacheSize())
                .maximumSize(compose.getCacheSize());
        if (compose.getCacheExpire() > 0) {
            build.expireAfterWrite(compose.getCacheExpire(), TimeUnit.MILLISECONDS);
        }
        var cache = build.build();
        return new CaffeineCache(cache);
    }

    static class CaffeineCache implements Cache {

        @SuppressWarnings("rawtypes")
        private final com.github.benmanes.caffeine.cache.Cache cache;

        @SuppressWarnings("rawtypes")
        public CaffeineCache(com.github.benmanes.caffeine.cache.Cache cache) {
            this.cache = cache;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object get(Object key) {
            return cache.getIfPresent(key);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void put(Object key, Object value) {
            cache.put(key, value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void evict(Object key) {
            cache.invalidate(key);
        }

        @Override
        public void clear() {
            cache.cleanUp();
        }
    }
}
