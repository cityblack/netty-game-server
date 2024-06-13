package com.lzh.game.framework.resource.inject;


import com.lzh.game.framework.resource.Storage;
import com.lzh.game.framework.resource.data.ResourceManageHandle;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultStorageManage implements StorageManage {

    private ResourceManageHandle resourceManageHandle;

    private Map<Class<?>, Storage<?,?>> storageMap = new ConcurrentHashMap<>();

    @Override
    public Storage<?, ?> getStorage(Class<?> clazz) {
        return storageMap.get(clazz);
    }

    protected Storage<?, ?> createStorage(Class<?> clazz) {

        return new DefaultStorage<>(clazz);
    }

    private class DefaultStorage<K extends Serializable,V> implements Storage<K,V> {

        private Class<V> referenceClass;

        @Override
        public List<V> getAll() {
            return resourceManageHandle.findAll(referenceClass);
        }

        @Override
        public V getUnique(String UniqueIndexName, Serializable value) {
            return resourceManageHandle.findOne(referenceClass, UniqueIndexName, value);
        }

        @Override
        public List<V> getIndex(String indexName, Serializable value) {
            return resourceManageHandle.findByIndex(referenceClass, indexName, value);
        }


        @Override
        public V get(K k) {
            return resourceManageHandle.findById(referenceClass, k);
        }

        @Override
        public V getOrThrow(K k) {
            V data = get(k);
            if (Objects.isNull(data)) {
                throw new NullPointerException(referenceClass.getSimpleName() + " no exist data with key is " + k);
            }
            return data;
        }

        protected DefaultStorage(Class<V> referenceClass) {
            this.referenceClass = referenceClass;
        }
    }

    public DefaultStorageManage(ResourceManageHandle resourceManageHandle) {
        this.resourceManageHandle = resourceManageHandle;
    }

    @Override
    public boolean containStorage(Class<?> type) {
        return storageMap.containsKey(type);
    }

    public void putStorage(Class<?> type, Storage storage) {
        storageMap.put(type, storage);
    }

}
