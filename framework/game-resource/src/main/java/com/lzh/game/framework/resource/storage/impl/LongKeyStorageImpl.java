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
public class LongKeyStorageImpl<V> extends AbstractStorage<Long, V> implements LongKeyStorage<V> {

    private LongObjectMap<V> contain;

    public LongKeyStorageImpl(ResourceLoadHandler loadHandle, ResourceMeta<V> meta) {
        super(loadHandle, meta);
    }

    @Override
    public V get(long id) {
        return contain.get(id);
    }

    @Override
    public List<V> getAll() {
        return List.copyOf(contain.values());
    }

    @Override
    public V get(Long id) {
        return contain.get(id);
    }

    @Override
    protected Map<Long, V> newContain() {
        return new LongObjectHashMap<>();
    }

    @Override
    protected void setContain(Map<Long, V> contain) {
        this.contain = (LongObjectMap<V>) contain;
    }
}
