package com.lzh.game.framework.repository;

import com.lzh.game.framework.repository.cache.CacheEntity;
import com.lzh.game.framework.repository.db.PersistEntity;

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
