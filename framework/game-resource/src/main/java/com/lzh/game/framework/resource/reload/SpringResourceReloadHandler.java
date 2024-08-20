package com.lzh.game.framework.resource.reload;

import com.lzh.game.framework.resource.data.meta.ResourceMetaManager;
import com.lzh.game.framework.resource.storage.manager.StorageManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class SpringResourceReloadHandler implements ResourceReloadHandler, ApplicationContextAware {

    private final Map<Class<?>, Set<ResourceReload>> contain = new HashMap<>();

    private final StorageManager storageManager;

    private final ResourceMetaManager resourceMetas;

    public SpringResourceReloadHandler(StorageManager storageManager, ResourceMetaManager resourceMetas) {
        this.storageManager = storageManager;
        this.resourceMetas = resourceMetas;
    }

    @Override
    public Set<ResourceReload> getReload(Class<?> resource) {
        return contain.getOrDefault(resource, Collections.emptySet());
    }

    @Override
    public Set<ResourceReload> getAllReload() {
        return contain.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    @Override
    public void addReload(Class<?> clz, ResourceReload resourceReload) {
        Set<ResourceReload> reloads = contain.computeIfAbsent(clz, e -> new HashSet<>());
        reloads.add(resourceReload);
        contain.put(clz, reloads);
    }

    @Override
    public void reloadByType(List<Class<?>> clz) {
        var list = new ArrayList<Class<?>>(clz.size());
        var reloadHandler = new HashSet<ResourceReload>();
        for (var c : clz) {
            var meta = resourceMetas.getResource(c);
            Assert.notNull(meta, "Not find " + c.getName());
            list.add(meta.getDataType());
            reloadHandler.addAll(getReload(meta.getDataType()));
        }
        this.reload(list, reloadHandler);
    }

    @Override
    public void reloadByName(List<String> resourceName) {
        var list = new ArrayList<Class<?>>(resourceName.size());
        var reloadHandler = new HashSet<ResourceReload>();
        for (String name : resourceName) {
            var meta = resourceMetas.getResource(name);
            Assert.notNull(meta, "Not find " + name);
            list.add(meta.getDataType());
            reloadHandler.addAll(getReload(meta.getDataType()));
        }
        this.reload(list, reloadHandler);
    }

    private void reload(List<Class<?>> types, Set<ResourceReload> resourceReload) {
        storageManager.reload(types);
        for (ResourceReload reload : resourceReload) {
            reload.reload(types);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        loadReload(applicationContext.getBeansOfType(ResourceReload.class));
    }

    private void loadReload(Map<String, ResourceReload> bean) {
        bean.forEach((k,v) -> {
            Class<?>[] classes = v.classes();
            for (Class<?> c: classes) {
                addReload(c, v);
            }
        });
    }
}
