package com.lzh.game.framework.repository;

import com.lzh.game.framework.repository.cache.CacheDataRepository;
import com.lzh.game.framework.repository.element.BaseEntity;
import com.lzh.game.framework.repository.persist.Element;
import com.lzh.game.framework.repository.persist.Persist;
import com.lzh.game.framework.repository.persist.PersistRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

/**
 * @param <PK>
 * @param <T>
 */
@Slf4j
public class DataRepositoryImpl<PK extends Serializable & Comparable<PK>, T extends BaseEntity<PK>>
        implements DataRepository<PK, T> {

    private Persist persist;

    private Class<T> cacheClass;

    private CacheDataRepository<PK, T> cache;

    private PersistRepository repository;

    private boolean clearMemAfterClose;

    public DataRepositoryImpl(CacheDataRepository<PK, T> cache, Persist persist, Class<T> cacheClass, PersistRepository repository, boolean clearMemAfterClose) {
        this.cache = cache;
        this.persist = persist;
        this.cacheClass = cacheClass;
        this.repository = repository;
        this.clearMemAfterClose = clearMemAfterClose;
    }

    @Override
    public T get(PK pk) {
        return cache.get(pk);
    }

    @Override
    public T load(PK pk) {
        T cache = get(pk);
        if (Objects.nonNull(cache)) {
            return cache;
        }
        synchronized (this) {
            cache = get(pk);
            if (Objects.isNull(cache)) {
                T entity = repository.findById(pk, this.cacheClass);
                if (Objects.nonNull(entity)) {
                    add(entity);
                    return entity;
                }
            }
            return cache;
        }
    }

    @Override
    public T loadOrCreate(PK pk, Function<PK, T> create) {
        T entity = load(pk);
        if (Objects.isNull(entity)) {
            synchronized (this) {
                entity = get(pk);
                if (Objects.isNull(entity)) {
                    entity = create.apply(pk);
                    add(entity);
                    saveToDb(entity);
                }
            }
        }
        return entity;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public T save(T entity) {
        cache.add(entity);
        persist.put(Element.saveOf(entity, this.cacheClass));
        return entity;
    }

    @Override
    public void add(T entity) {
        cache.add(entity);
    }

    @Override
    public void deleteMem(PK pk) {
        cache.deleteMem(pk);
    }

    @Override
    public void deleter(PK pk) {
        T data = load(pk);
        if (Objects.nonNull(data)) {
            deleteMem(pk);
            persist.put(Element.deleterOf(data, this.cacheClass));
        }
    }

    @Override
    public void shutdown() {
        if (clearMemAfterClose) {
            this.clear();
        }
        persist.shutDown();
    }

    private void saveToDb(T entity) {
        this.persist.put(Element.saveOf(entity, this.cacheClass));
    }
}
