package com.lzh.game.framework.repository.cache.redis;

import com.lzh.game.framework.repository.cache.CacheFactory;
import com.lzh.game.framework.repository.cache.Cache;
import com.lzh.game.framework.repository.config.ComposeConfig;
import com.lzh.game.framework.repository.config.RepositoryConfig;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonCache;

/**
 * @author zehong.l
 * @since 2024-10-12 12:08
 **/
public class RedissonCacheFactory implements CacheFactory {

    private final RedissonClient client;

    private final RepositoryConfig config;

    private Codec codec;

    public RedissonCacheFactory(RedissonClient client, RepositoryConfig config) {
        this.client = client;
        this.config = config;
    }

    public RedissonCacheFactory(RedissonClient client, RepositoryConfig config, Codec codec) {
        this.client = client;
        this.config = config;
        this.codec = codec;
    }

    @Override
    public Cache createCache(String name, Class<?> type) {
        return new RCache(crateRCache(name, type, config, client));
    }

    private RedissonCache crateRCache(String name, Class<?> type, RepositoryConfig config, RedissonClient client) {
        var compose = new ComposeConfig(config, type);
        var rConfig = new CacheConfig(compose.getCacheExpire(), 0);
        rConfig.setMaxSize(compose.getCacheSize());
        return new RedissonCache(getMapCache(name), rConfig, true);
    }

    protected RMapCache<Object, Object> getMapCache(String name) {
        if (codec != null) {
            return client.getMapCache(name, codec);
        }
        return client.getMapCache(name);
    }

    static class RCache implements Cache {

        private final RedissonCache cache;

        public RCache(RedissonCache cache) {
            this.cache = cache;
        }

        @Override
        public Object get(Object key) {
            return cache.get(key);
        }

        @Override
        public void put(Object key, Object value) {
            cache.put(key, value);
        }

        @Override
        public void evict(Object key) {
            cache.evict(key);
        }

        @Override
        public void clear() {
            cache.clear();
        }
    }
}
