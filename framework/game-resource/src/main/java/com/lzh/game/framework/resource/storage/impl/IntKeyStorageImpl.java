package com.lzh.game.framework.resource.storage.impl;

import com.lzh.game.framework.resource.data.load.ResourceLoadHandler;
import com.lzh.game.framework.resource.data.meta.ResourceMeta;
import com.lzh.game.framework.resource.storage.IntKeyStorage;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

import java.util.List;
import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-08-20 12:06
 **/
public class IntKeyStorageImpl<V> extends AbstractStorage<Integer, V, IntObjectMap<V>> implements IntKeyStorage<V> {

    public IntKeyStorageImpl(ResourceLoadHandler loadHandle, ResourceMeta<V> meta) {
        super(loadHandle, meta);
    }

    @Override
    public V get(int id) {
        return getContain().get(id);
    }

    @Override
    public V get(Integer integer) {
        return getContain().get(integer);
    }

    @Override
    protected IntObjectMap<V> newContain() {
        return new IntObjectHashMap<>();
    }

}
