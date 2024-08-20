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
public class IntKeyStorageImpl<V> extends AbstractStorage<Integer, V> implements IntKeyStorage<V> {

    private IntObjectMap<V> contain;

    public IntKeyStorageImpl(ResourceLoadHandler loadHandle, ResourceMeta<V> meta) {
        super(loadHandle, meta);
    }

    @Override
    public V get(int id) {
        return contain.get(id);
    }

    @Override
    public List<V> getAll() {
        return List.copyOf(contain.values());
    }

    @Override
    public V get(Integer integer) {
        return contain.get(integer);
    }


    @Override
    protected Map<Integer, V> newContain() {
        return new IntObjectHashMap<>();
    }

    @Override
    protected void setContain(Map<Integer, V> contain) {
        this.contain = (IntObjectMap<V>) contain;
    }

}
