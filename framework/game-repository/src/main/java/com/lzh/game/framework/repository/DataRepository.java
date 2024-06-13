package com.lzh.game.framework.repository;

import com.lzh.game.framework.repository.cache.CacheDataRepository;
import com.lzh.game.framework.repository.db.PersistEntity;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Cache and db compose
 *
 * @param <PK> -- entity key type
 * @param <T>  -- BaseEntity
 */
public interface DataRepository<PK extends Serializable & Comparable<PK>, T extends BaseEntity<PK>>
        extends CacheDataRepository<PK, T> {

    /**
     * Add to mem and db
     *
     * @param entity
     */
    T save(T entity);

    /**
     * According PK to find the mapping data while one is null will load from db
     *
     * @param pk
     * @return
     */
    T load(PK pk);

    /**
     * @param pk
     * @param create
     * @return
     */
    T loadOrCreate(PK pk, Function<PK, T> create);

    /**
     * Update cache proxy data to mem. If data entity extends {@link PersistEntity} while update to db
     *
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
    void deleter(PK pk);

    default void deleter(T data) {
        deleter(data.cacheKey());
    }
}
