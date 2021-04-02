package com.lzh.game.repository;

import com.lzh.game.repository.cache.CacheEntity;

import java.io.Serializable;
import java.util.stream.Stream;

public interface LoadDataRepository<PK extends Serializable & Comparable<PK>, T extends CacheEntity<PK>> {
    /**
     * Find data by the PK value
     * @return In memory data. May be null
     */
    T get(PK pk);


    /**
     * Load data from mem
     * @return
     */
    Stream<T> getAll();
}
