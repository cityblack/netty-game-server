package com.lzh.game.framework.repository.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.Serializable;

/**
 * @author zehong.l
 * @since 2024-06-13 12:39
 **/
public class CacheDataRepositoryImpl<PK extends Serializable & Comparable<PK>, T extends CacheEntity<PK>> implements CacheDataRepository<PK, T> {

    private CacheManager cache;

    private Class<T> cacheClass;

    private String cacheName;

    public CacheDataRepositoryImpl(CacheManager cache, Class<T> cacheClass) {
        this.cache = cache;
        this.cacheClass = cacheClass;
        this.cacheName = cacheClass.getSimpleName();
    }

    @Override
    public T get(PK pk) {
        return getCache().get(pk, cacheClass);
    }

    @Override
    public void add(T entity) {
        PK key = entity.cacheKey();
        getCache().put(key, entity);
    }

    @Override
    public void deleteMem(PK pk) {
        getCache().evict(pk);
    }

    @Override
    public void clear() {
        getCache().clear();
    }

    private Cache getCache() {
        return cache.getCache(this.cacheName);
    }
}
