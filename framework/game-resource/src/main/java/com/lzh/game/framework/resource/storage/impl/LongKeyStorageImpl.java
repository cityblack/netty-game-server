package com.lzh.game.framework.resource.storage.impl;

import com.lzh.game.framework.resource.data.load.ResourceLoadHandler;
import com.lzh.game.framework.resource.data.meta.ResourceMeta;
import com.lzh.game.framework.resource.storage.LongKeyStorage;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;

import java.util.List;
import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-08-20 12:11
 **/
public class LongKeyStorageImpl<V> extends AbstractStorage<Long, V, LongObjectMap<V>> implements LongKeyStorage<V> {

    public LongKeyStorageImpl(ResourceLoadHandler loadHandle, ResourceMeta<V> meta) {
        super(loadHandle, meta);
    }

    @Override
    public V get(long id) {
        return getContain().get(id);
    }

    @Override
    public V get(Long id) {
        return getContain().get(id);
    }

    @Override
    protected LongObjectMap<V> newContain() {
        return new LongObjectHashMap<>();
    }
}
