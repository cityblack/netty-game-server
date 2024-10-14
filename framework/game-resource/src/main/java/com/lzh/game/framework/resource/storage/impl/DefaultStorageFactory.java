package com.lzh.game.framework.resource.storage.impl;

import com.lzh.game.framework.resource.data.load.ResourceLoadHandler;
import com.lzh.game.framework.resource.data.meta.ResourceMeta;
import com.lzh.game.framework.resource.storage.Storage;
import com.lzh.game.framework.resource.storage.StorageFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zehong.l
 * @since 2024-08-20 12:35
 **/
public class DefaultStorageFactory implements StorageFactory {

    private final ResourceLoadHandler loadHandle;

    public DefaultStorageFactory(ResourceLoadHandler loadHandle) {
        this.loadHandle = loadHandle;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K extends Serializable, V> Storage<K, V> createStorage(ResourceMeta<V> meta) {
        var type = meta.getId().getType();
        if (type == int.class || type == Integer.class) {
            return (Storage<K, V>) new IntKeyStorageImpl<>(loadHandle, meta);
        } else if (type == long.class || type == Long.class) {
            return (Storage<K, V>) new LongKeyStorageImpl<>(loadHandle, meta);
        }
        return new DefaultStorage<>(loadHandle, meta);
    }

    private static class DefaultStorage<K extends Serializable, V> extends AbstractStorage<K, V, Map<K, V>> {

        public DefaultStorage(ResourceLoadHandler loadHandle, ResourceMeta<V> meta) {
            super(loadHandle, meta);
        }

        @Override
        public List<V> getAll() {
            return List.copyOf(getContain().values());
        }

        @Override
        public V get(K k) {
            return getContain().get(k);
        }

        @Override
        protected Map<K, V> newContain() {
            return new HashMap<>();
        }

    }
}
