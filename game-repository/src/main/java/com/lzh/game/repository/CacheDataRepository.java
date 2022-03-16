package com.lzh.game.repository;

import com.lzh.game.repository.cache.CacheEntity;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Cache data
 * @param <PK>
 * @param <T>
 */
public interface CacheDataRepository<PK extends Serializable & Comparable<PK>, T extends CacheEntity<PK>> {
    /**
     * Find data by the PK value
     *
     * @return In memory data. May be null
     */
    T get(PK pk);

    /**
     * Add to mem
     * @param entity
     */
    void add(T entity);

    /**
     * clear the cache all data
     */
    void clear();
}
