package com.lzh.game.framework.repository;

import com.lzh.game.framework.repository.cache.CacheEntity;
import com.lzh.game.framework.repository.db.Element;
import com.lzh.game.framework.repository.db.Persist;
import com.lzh.game.framework.repository.db.PersistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

/**
 * Find data from cache. If can't find. find data from db
 * @param <PK>
 * @param <T>
 */
@Slf4j
public class DataRepositoryImpl<PK extends Serializable & Comparable<PK>, T extends BaseEntity<PK>>
        implements DataRepository<PK, T>, DisposableBean {

    private CacheManager cache;

    private Persist persist;

    private Class<T> cacheClass;

    private String cacheName;

    private PersistRepository repository;

    private boolean clearMemAfterClose;

    public DataRepositoryImpl(CacheManager cache, Persist persist, Class<T> cacheClass, PersistRepository repository, boolean clearMemAfterClose) {
        this.cache = cache;
        this.persist = persist;
        this.cacheClass = cacheClass;
        this.cacheName = cacheClass.getSimpleName();
        this.repository = repository;
        this.clearMemAfterClose = clearMemAfterClose;
    }

    @Override
    public T get(PK pk) {
        return getCache().get(pk, cacheClass);
    }

    @Override
    public T load(PK pk) {
        T cache = get(pk);
        if (Objects.nonNull(cache)) {
            return cache;
        }
        T entity = repository.findById(pk, this.cacheClass);
        if (Objects.nonNull(entity)) {
            addCache(entity);
        }
        return entity;
    }

    @Override
    public T loadOrCreate(PK pk, Function<PK, T> create) {
        T entity = load(pk);
        if (Objects.isNull(entity)) {
            entity = create.apply(pk);
            addCache(entity);
            saveToDb(entity);
        }
        return entity;
    }

    @Override
    public void destroy() throws Exception {
        if (clearMemAfterClose) {
            this.clear();
        }
    }

    @Override
    public void clear() {
        getCache().clear();
    }

    @Override
    public T save(T entity) {
        addCache(entity);
        persist.put(Element.saveOf(entity, this.cacheClass));
        return entity;
    }

    @Override
    public void add(T entity) {
        addCache(entity);
    }

    private Cache getCache() {
        return cache.getCache(this.cacheName);
    }

    @Override
    public void update(PK pk, T data) {

        if (Objects.isNull(data)) {
            return;
        }
        getCache().put(pk, data);
        persist.put(Element.saveOf(data, data.getClass()));
    }

    @Override
    public T deleter(PK pk) {
        T data = load(pk);
        if (Objects.nonNull(data)) {
            persist.put(Element.deleterOf(data, this.cacheClass));
            this.getCache().evict(pk);
        }
        return data;
    }

    private void addCache(CacheEntity<PK> cacheEntity) {
        PK key = cacheEntity.cacheKey();
        getCache().put(key, cacheEntity);
    }

    private void saveToDb(T entity) {
        this.persist.put(Element.saveOf(entity, this.cacheClass));
    }
}
