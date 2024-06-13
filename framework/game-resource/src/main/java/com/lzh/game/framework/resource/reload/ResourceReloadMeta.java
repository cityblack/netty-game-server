package com.lzh.game.framework.resource.reload;

import com.lzh.game.framework.resource.ResourceReload;

import java.util.Set;

public interface ResourceReloadMeta {

    Set<ResourceReload> getReload(Class<?> resource);

    Set<ResourceReload> getAllReload();
}
