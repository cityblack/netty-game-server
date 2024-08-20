package com.lzh.game.framework.resource.storage;

import com.lzh.game.framework.resource.data.meta.ResourceMeta;

import java.io.Serializable;

/**
 * @author zehong.l
 * @since 2024-08-20 11:53
 **/
public interface StorageFactory {

    <K extends Serializable, V> Storage<K, V> createStorage(ResourceMeta<V> meta);
}
