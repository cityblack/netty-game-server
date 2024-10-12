package com.lzh.game.framework.repository.element;

import com.lzh.game.framework.repository.persist.PersistEntity;

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
