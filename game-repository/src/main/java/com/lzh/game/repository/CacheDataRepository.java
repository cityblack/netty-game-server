package com.lzh.game.repository;

import com.lzh.game.repository.db.PersistEntity;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.function.Function;

/**
 *
 */
public interface CacheDataRepository extends LoadDataRepository, PersistDataRepository {

    /**
     * Enhance {@link #loadOrCreate}. If not find the key's value will load it from repository
     * @param pk
     * @param clazz
     * @param repository
     * @param create If not find the value from cache and db, will call the function to build data
     * @param <T>
     * @param <PK>
     * @return
     */
    default <T extends PersistEntity, PK extends Serializable>T enhanceLoadOrCreate(PK pk, Class<T> clazz, CrudRepository<T, PK> repository, Function<PK, T> create) {
        T entity = loadOrCreate(pk, clazz, e -> repository.findById(e).orElseGet(() -> create.apply(pk)));
        entity.deserialize();
        return entity;
    }

    /**
     *
     */
    void clear();
}
