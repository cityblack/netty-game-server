package com.lzh.game.resource.reload;

import com.lzh.game.resource.ResourceReload;

import java.util.Set;

public interface ResourceReloadMeta {

    Set<ResourceReload> getReload(Class<?> resource);

    Set<ResourceReload> getAllReload();
}
