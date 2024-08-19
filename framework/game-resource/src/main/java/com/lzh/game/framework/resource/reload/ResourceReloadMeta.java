package com.lzh.game.framework.resource.reload;

import java.util.Set;

public interface ResourceReloadMeta {

    Set<ResourceReload> getReload(Class<?> resource);

    Set<ResourceReload> getAllReload();
}
