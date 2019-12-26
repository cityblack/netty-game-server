package com.lzh.game.repository;

import com.lzh.game.repository.cache.CacheEntity;
import com.lzh.game.repository.db.PersistEntity;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.function.Function;
import java.util.stream.Stream;

public interface LoadDataRepository {
    /**
     * Find data by the PK value
     * @param pk
     * @param clazz
     * @param <T>
     * @param <PK>
     * @return In memory data. May be null
     */
    <T extends CacheEntity, PK extends Serializable>T get(PK pk, Class<T> clazz);

    /**
     * According the PK to find the mapping data while one is null will process load function
     * @param pk
     * @param clazz
     * @param load -- when return value is null
     * @param <T>
     * @param <PK>
     * @return
     */
    <T extends CacheEntity, PK extends Serializable>T load(PK pk, Class<T> clazz, Function<PK, T> load);

    /**
     * It like {@link #load(Serializable, Class, Function)} while the method will save data to db
     * @param pk
     * @param clazz
     * @param create
     * @param <T>
     * @param <PK>
     * @return
     */
    <T extends PersistEntity, PK extends Serializable>T loadOrCreate(PK pk, Class<T> clazz, Function<PK, T> create);

    /**
     * Load data from mem or db
     * @param clazz
     * @param repository
     * @param <T>
     * @param <PK>
     * @return
     */
    <T extends PersistEntity, PK extends Serializable>Stream<T> loadAll(Class<T> clazz, CrudRepository<T, PK> repository);

    /**
     * Load data from mem
     * @param clazz
     * @param <T>
     * @return
     */
    <T extends CacheEntity>Stream<T> getAll(Class<T> clazz);
}
