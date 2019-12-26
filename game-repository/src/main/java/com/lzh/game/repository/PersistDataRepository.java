package com.lzh.game.repository;

import com.lzh.game.repository.cache.CacheEntity;
import com.lzh.game.repository.db.PersistEntity;

import java.io.Serializable;

public interface PersistDataRepository {

    /**
     * Update cache proxy data to mem. If data entity extends {@link PersistEntity} while update to db
     * @param pk
     * @param data
     * @param <PK>
     */
    <PK extends Serializable>void update(PK pk, CacheEntity data);

    /**
     * {@link #update(Serializable, CacheEntity)}
     * @param data
     */
    default void update(CacheEntity data) {
        update(data.cacheKey(), data);
    }

    /**
     * Deleter data from mem. If the class extends {@link PersistEntity} will persist to db
     * @param pk
     * @param <PK>
     */
    <T extends CacheEntity, PK extends Serializable>T deleter(PK pk, Class<T> clazz);

    /**
     * Add data to cache, The method can add any type data to mem. while {@link #update(Serializable, CacheEntity)}
     * @param pk
     * @param data
     * @param <PK>
     */
    <PK extends Serializable> void add(PK pk, Object data);

    /**
     * Add cacheEntity entity to mem
     * @param cacheEntity
     */
    default CacheEntity add(CacheEntity cacheEntity) {
        add(cacheEntity.cacheKey(), cacheEntity);
        return cacheEntity;
    }
    /**
     * Add data to mem and persist to db
     * @param pk
     * @param data
     * @param <T>
     * @param <PK>
     */
    <T extends PersistEntity, PK extends Serializable> T addAndSave(PK pk, T data);
}
