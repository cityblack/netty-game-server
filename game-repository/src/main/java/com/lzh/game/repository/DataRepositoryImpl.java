package com.lzh.game.repository;

import com.lzh.game.repository.cache.CacheEntity;
import com.lzh.game.repository.db.Element;
import com.lzh.game.repository.db.Persist;
import com.lzh.game.repository.db.PersistRepository;
import io.netty.util.internal.ConcurrentSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public class DataRepositoryImpl<PK extends Serializable & Comparable<PK>, T extends BaseEntity<PK>>
        implements DataRepository<PK, T>, DisposableBean {

    private CacheManager cache;

    private Persist persist;

    private Class<T> cacheClass;

    private String cacheName;

    private PersistRepository repository;

    private Set<PK> loadedKeys = new ConcurrentSet<>();

    private boolean clearMemAfterClose;

    private volatile boolean loadedAll = Boolean.FALSE;

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
        addCache(entity);
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
    public Stream<T> loadAll() {
        if (this.loadedAll) {
            return getAll();
        }
        this.loadedAll = true;
        return this.repository
                .findAll(this.cacheClass)
                .stream()
                .peek(this::addCache);
    }

    @Override
    public Stream<T> getAll() {
        return this.loadedKeys.stream().map(this::load);
    }

    @Override
    public void destroy() throws Exception {
        if (clearMemAfterClose) {
            this.clear();
        }
    }

    @Override
    public void clear() {
        this.loadedKeys.clear();
        getCache().clear();
    }

    @Override
    public T save(T entity) {
        addCache(entity);
        persist.put(Element.saveOf(entity, this.cacheClass));
        return entity;
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
            this.loadedKeys.remove(pk);
        }
        return data;
    }

    private void addCache(CacheEntity<PK> cacheEntity) {
        PK key = cacheEntity.cacheKey();
        this.loadedKeys.add(key);
        getCache().put(key, cacheEntity);
    }

    private void saveToDb(T entity) {
        this.persist.put(Element.saveOf(entity, this.cacheClass));
    }
}
