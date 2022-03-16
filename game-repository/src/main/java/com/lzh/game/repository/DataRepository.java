package com.lzh.game.repository;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Cache and db compose
 * @param <PK> -- entity key type
 * @param <T> -- BaseEntity
 */
public interface DataRepository<PK extends Serializable & Comparable<PK>, T extends BaseEntity<PK>>
        extends CacheDataRepository<PK, T>, UpdateDataRepository<PK, T> {

    /**
     * Add to mem and db
     * @param entity
     */
    T save(T entity);

    /**
     * According the PK to find the mapping data while one is null will load from db
     *
     * @param pk
     * @return
     */
    T load(PK pk);

    /**
     *
     * @param pk
     * @param create
     * @return
     */
    T loadOrCreate(PK pk, Function<PK, T> create);
}
