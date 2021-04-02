package com.lzh.game.repository;

import com.lzh.game.repository.db.PersistEntity;

import java.io.Serializable;

public interface UpdateDataRepository<PK extends Serializable & Comparable<PK>, T extends BaseEntity<PK>> {

    /**
     * Update cache proxy data to mem. If data entity extends {@link PersistEntity} while update to db
     * @param pk
     * @param data
     */
    void update(PK pk, T data);

    /**
     * @param data
     */
    default void update(T data) {
        update(data.cacheKey(), data);
    }

    /**
     * Deleter data from mem. If the class extends {@link PersistEntity} will persist to db
     */
    T deleter(PK pk);

    default T deleter(T data) {
        return deleter(data.cacheKey());
    }
}
