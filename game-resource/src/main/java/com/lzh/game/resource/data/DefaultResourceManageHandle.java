package com.lzh.game.resource.data;

import com.lzh.game.resource.ResourceLoaded;
import com.lzh.game.resource.data.cache.ResourceCache;
import com.lzh.game.resource.data.cache.ResourceCacheFactory;
import com.lzh.game.resource.reload.ResourceReloadMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DefaultResourceManageHandle implements ResourceManageHandle {

    private LoadResourceHandle loadResourceHandle;

    private ResourceCacheFactory cacheFactory;

    private ResourceReloadMeta reloadMeta;

    private ResourceModelMeta modelManage;

    private Map<Class<?>, ResourceCache<Serializable, ?>> caches = new HashMap<>();

    public DefaultResourceManageHandle(LoadResourceHandle loadResourceHandle, ResourceCacheFactory cacheFactory, ResourceReloadMeta reloadMeta, ResourceModelMeta modelManage) {
        this.loadResourceHandle = loadResourceHandle;
        this.cacheFactory = cacheFactory;
        this.reloadMeta = reloadMeta;
        this.modelManage = modelManage;
    }

    @Override
    public <T> List<T> findAll(@NonNull Class<T> clazz) {
        ResourceCache<Serializable, ?> cache = caches.get(clazz);
        return Objects.nonNull(cache) ? (List<T>) cache.findAll() : Collections.emptyList();
    }

    @Override
    public <T> List<T> findByIndex(Class<T> clazz, String indexName, Serializable value) {
        ResourceCache<Serializable, ?> cache = caches.get(clazz);
        return Objects.nonNull(cache) ? (List<T>) cache.findByIndex(indexName, value) : Collections.emptyList();
    }


    @Override
    public <T> T findOne(Class<T> clazz, String indexName, Serializable value) {
        ResourceCache<Serializable, ?> cache = caches.get(clazz);
        return Objects.nonNull(cache) ? (T) cache.findOne(indexName, value) : null;
    }

    @Override
    public <T> T findById(Class<T> clazz, Serializable k) {
        ResourceCache<Serializable, ?> cache = caches.get(clazz);
        return Objects.nonNull(cache) ? (T) cache.findById(k) : null;
    }

    @Override
    public void reload() {
        clear();
        modelManage.forEach(e -> flashData(e.getDataType()));
        Class<?>[] clazz = modelManage.getAllResourceType().toArray(Class<?>[]::new);
        reloadMeta.getAllReload().forEach(e -> e.reload(clazz));
    }

    @Override
    public void reload(Class<?>[] clazz) {
        // 先全部加载完成 再执行reload方法 防止reload实现有互相依赖关系出错中断流程
        Stream.of(clazz).peek(this::reload)
                .flatMap(c -> reloadMeta.getReload(c).stream())
                .collect(Collectors.toList())
                .forEach(e -> e.reload(clazz));
    }

    protected void reload(Class<?> clazz) {
        clear(clazz);
        flashData(clazz);
    }

    private void flashData(Class<?> type) {
        ResourceModel model = modelManage.getResource(type);
        List list = loadResourceHandle.loadList(type, model.getResourceName());
        ResourceCache<Serializable, ?> cache = this.caches.computeIfAbsent(type,
                (k) -> cacheFactory.newCache(k, model));
        cache.put(list, model, this::putData);
    }

    protected void putData(Object data) {
        if (data instanceof ResourceLoaded) {
            ((ResourceLoaded) data).loaded();
        }
    }

    protected void clear() {
        for (Map.Entry<Class<?>, ResourceCache<Serializable, ?>> entry : this.caches.entrySet()) {
            entry.getValue().clear();
        }
    }

    protected void clear(Class<?> key) {
        ResourceCache<?, ?> cache = this.caches.get(key);
        if (Objects.nonNull(cache)) {
            cache.clear();
        }
    }
}
