package com.lzh.game.repository;

import java.io.Serializable;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 */
public interface DataRepository<PK extends Serializable & Comparable<PK>, T extends BaseEntity<PK>>
        extends LoadDataRepository<PK, T>, UpdateDataRepository<PK, T> {
    /**
     * According the PK to find the mapping data while one is null will process load function
     * @param pk
     * @return
     */
    T load(PK pk);
    /**
     *  The method will save data to db
     * @param pk
     * @param create
     * @return
     */
    T loadOrCreate(PK pk, Function<PK, T> create);

    /**
     * Load data from mem or db
     * @return
     */
    Stream<T> loadAll();
    /**
     *
     */
    void clear();

    /**
     * Add to mem and db
     * @param entity
     */
    T save(T entity);
}
