package com.lzh.game.repository;

import com.lzh.game.repository.cache.CacheEntity;
import com.lzh.game.repository.db.PersistEntity;

import java.io.Serializable;

/**
 * Base entity
 * @param <PK>
 */
public abstract class BaseEntity<PK extends Serializable & Comparable<PK>> implements CacheEntity<PK>, PersistEntity<PK> {

    @Override
    public PK cacheKey() {
        return getKey();
    }
}
