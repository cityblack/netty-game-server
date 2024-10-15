package com.lzh.game.framework.resource.reload;

import java.util.List;
import java.util.Set;

public interface ResourceReloadManager {

    Set<ResourceReload> getReload(Class<?> resource);

    Set<ResourceReload> getAllReload();

    void addReload(Class<?> clz, ResourceReload resourceReload);

    void reloadByType(List<Class<?>> clz);

    void reloadByName(List<String> resourceName);

}
