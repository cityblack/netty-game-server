package com.lzh.game.resource.reload;

import com.lzh.game.resource.ResourceReload;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;
import java.util.stream.Collectors;

public class SpringResourceReloadMeta implements ResourceReloadMeta, ApplicationContextAware {

    private Map<Class<?>, Set<ResourceReload>> contain = new HashMap<>();

    @Override
    public Set<ResourceReload> getReload(Class<?> resource) {
        return contain.getOrDefault(resource, Collections.EMPTY_SET);
    }

    @Override
    public Set<ResourceReload> getAllReload() {
        return contain.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        loadReload(applicationContext.getBeansOfType(ResourceReload.class));
    }

    private void loadReload(Map<String, ResourceReload> bean) {
        bean.forEach((k,v) -> {
            Class<?>[] classes = v.classes();
            for (Class<?> c: classes) {
                Set<ResourceReload> reloads = contain.getOrDefault(c, new HashSet<>());
                reloads.add(v);
                contain.put(c, reloads);
            }
        });
    }
}
