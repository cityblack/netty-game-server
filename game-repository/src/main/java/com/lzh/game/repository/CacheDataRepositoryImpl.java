package com.lzh.game.repository;

import com.lzh.game.repository.cache.CacheEntity;
import com.lzh.game.repository.db.Element;
import com.lzh.game.repository.db.Persist;
import com.lzh.game.repository.db.PersistEntity;
import io.netty.util.internal.ConcurrentSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public class CacheDataRepositoryImpl implements CacheDataRepository, DisposableBean {

    private CacheManager cache;

    private Persist persist;

    private boolean clearMemAfterClose;
    /**
     * Use to remain the classes' keys
     */
    private transient Map<Class<?>, Set<Serializable>> dataKeyCache = new ConcurrentHashMap<>();

    public CacheDataRepositoryImpl(CacheManager cache, Persist persist) {
        this.cache = cache;
        this.persist = persist;
    }

    @Override
    public <T extends CacheEntity, PK extends Serializable> T get(PK pk, Class<T> clazz) {

        return findCache(pk, clazz);
    }

    @Override
    public <T extends CacheEntity, PK extends Serializable> T load(PK pk, Class<T> clazz, Function<PK, T> load) {
        T entity = get(pk, clazz);
        if (Objects.nonNull(entity)) {
            return entity;
        }
        entity = load.apply(pk);
        if (Objects.isNull(entity)) {
            return entity;
        }
        add(pk, entity);
        return get(pk, clazz);
    }

    @Override
    public <T extends PersistEntity, PK extends Serializable> T loadOrCreate(PK pk, Class<T> clazz, Function<PK, T> create) {

        T entity = get(pk, clazz);
        if (Objects.nonNull(entity)) {
            return entity;
        }
        entity = create.apply(pk);
        if (Objects.isNull(entity)) {
            return entity;
        }
        add(pk, entity);
        save(entity, clazz);
        return get(pk, clazz);
    }

    @Override
    public <T extends PersistEntity, PK extends Serializable> Stream<T> loadAll(Class<T> clazz, CrudRepository<T, PK> repository) {

        if (!this.dataKeyCache.containsKey(clazz)) {
            repository.findAll().forEach(e -> {
                deserialize(e);
                add(e.cacheKey(), e);
            });
        }
        return getAll(clazz);
    }

    @Override
    public <T extends CacheEntity> Stream<T> getAll(Class<T> clazz) {
        return dataKeyCache.getOrDefault(clazz, new HashSet<>()).parallelStream()
                .map(e -> get(e, clazz))
                .filter(Objects::nonNull);
    }

    @Override
    public <PK extends Serializable> void update(PK pk, CacheEntity data) {

        if (Objects.isNull(data)) {
            return;
        }

        getCache(data).put(pk, data);
        if (data instanceof PersistEntity) {
            persist.put(Element.saveOf((PersistEntity)data, (Class<? extends PersistEntity>) data.getClass()));
        }

    }

    @Override
    public void clear() {
        dataKeyCache.clear();
        cache.getCacheNames().forEach(e -> {
            if (log.isDebugEnabled()) {
                log.debug("Clear {} data..", e);
            }
            cache.getCache(e).clear();
        });
    }

    @Override
    public <T extends CacheEntity, PK extends Serializable> T deleter(PK pk, Class<T> clazz) {
        T data = get(pk, clazz);

        if (isPersist(clazz)) {
            addPersistQueue(Element.deleterOf((PersistEntity)data, (Class<? extends PersistEntity>) clazz));
        }

        this.getCache(clazz).evict(pk);
        getClassKey(data.getClass()).remove(pk);
        return data;
    }

    @Override
    public <PK extends Serializable> void add(PK pk, Object data) {

        Objects.requireNonNull(data, "Add to redis value is null. key:" + pk);
        if (log.isDebugEnabled()) {
            log.debug("Add [{} -> {}] to redis", pk, data);
        }
        this.getCache(data).put(pk, data);
        getClassKey(data.getClass()).add(pk);
    }

    @Override
    public <T extends PersistEntity, PK extends Serializable> T addAndSave(PK pk, T data) {
        add(pk, data);
        save(data, (Class<T>) getSourceClass(data));
        return data;
    }

    private <T extends PersistEntity> void save(T data, Class<T> clazz) {

        Objects.requireNonNull(data, "Add to db value is null");
        if (data.serialize()) {
            addPersistQueue(Element.saveOf(data, clazz));
        }
    }

    private void addPersistQueue(Element element) {
        persist.put(element);
    }

    private <T>boolean isPersist(Class<T> clazz) {
        return PersistEntity.class.equals(clazz);
    }

    private Cache getCache(Class<?> clazz) {

        return cache.getCache(clazz.getSimpleName());
    }

    private Cache getCache(Object data) {

        return getCache(getSourceClass(data));
    }

    private Class<?> getSourceClass(Object data) {

        return data.getClass();
    }

    @Override
    public void destroy() throws Exception {

        if (clearMemAfterClose) {
            clear();
        }
    }

    public void setClearMemAfterClose(boolean clearMemAfterClose) {
        this.clearMemAfterClose = clearMemAfterClose;
    }

    /**
     * Get classes' keys to find data
     * @param clazz
     * @return
     */
    private Set<Serializable> getClassKey(Class<?> clazz) {
        Set<Serializable> set = this.dataKeyCache.get(clazz);
        if (Objects.isNull(set)) {
            set = new ConcurrentSet<>();
            this.dataKeyCache.put(clazz, set);
        }
        return set;
    }

    private void deserialize(PersistEntity entity) {
        entity.deserialize();
    }

    private <T extends CacheEntity>T findCache(Serializable key, Class<T> c) {

       return getCache(c).get(key, c);
    }
}
