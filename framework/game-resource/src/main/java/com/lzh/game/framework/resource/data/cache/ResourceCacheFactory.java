package com.lzh.game.framework.resource.data.cache;

import com.lzh.game.framework.resource.data.ResourceMeta;

import java.io.Serializable;

public interface ResourceCacheFactory {

    <K extends Serializable ,T> ResourceCache<K, T> newCache(Class<T> type, ResourceMeta model);
}
