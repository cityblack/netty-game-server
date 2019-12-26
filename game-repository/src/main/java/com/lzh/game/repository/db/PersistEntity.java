package com.lzh.game.repository.db;

import com.lzh.game.repository.cache.CacheEntity;

import java.io.Serializable;

public abstract class PersistEntity<PK extends Serializable & Comparable<PK>> implements CacheEntity<PK> {

    /**
     * Serialize function. When the return value is {@code true}, will not persist the entity
     * @return
     */
    public boolean serialize() {
        return true;
    }

    /**
     * When data loaded from db. Please call the function
     */
    public void deserialize() {}
}
