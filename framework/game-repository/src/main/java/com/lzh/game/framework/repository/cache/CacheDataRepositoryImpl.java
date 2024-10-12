package com.lzh.game.framework.repository.cache;


import com.lzh.game.framework.repository.element.CacheEntity;

import java.io.Serializable;

/**
 * @author zehong.l
 * @since 2024-06-13 12:39
 **/
public class CacheDataRepositoryImpl<PK extends Serializable & Comparable<PK>, T extends CacheEntity<PK>> implements CacheDataRepository<PK, T> {

    private final Cache cache;

    private final Class<T> cacheClass;

    private final String cacheName;

    public CacheDataRepositoryImpl(String name, Cache cache, Class<T> cacheClass) {
        this.cache = cache;
        this.cacheClass = cacheClass;
        this.cacheName = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(PK pk) {
        return (T) getCache().get(pk);
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
        return cache;
    }
}
