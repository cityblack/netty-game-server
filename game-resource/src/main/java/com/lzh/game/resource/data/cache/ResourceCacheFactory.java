package com.lzh.game.resource.data.cache;

import com.lzh.game.resource.data.ResourceModel;

import java.io.Serializable;

public interface ResourceCacheFactory {

    <K extends Serializable ,T> ResourceCache<K, T> newCache(Class<T> type, ResourceModel model);
}
