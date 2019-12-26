package com.lzh.game.resource.inject;


import com.lzh.game.resource.Storage;
import com.lzh.game.resource.data.ResourceManageHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultStorageManage implements StorageManage {

    private ResourceManageHandler resourceManageHandler;

    private Map<Class<?>, Storage<?,?>> storageMap = new ConcurrentHashMap<>();

    @Override
    public Storage<?, ?> getStorage(Class<?> clazz) {
        return storageMap.get(clazz);
    }

    protected Storage<?, ?> createStorage(Class<?> clazz) {

        return new DefaultStorage<>(clazz);
    }

    private class DefaultStorage<K,V> implements Storage<K,V> {

        private Class<V> referenceClass;

        @Override
        public List<V> getAll() {
            return resourceManageHandler.findAll(referenceClass);
        }

        @Override
        public V getUnique(String uniqueIndex, Object value) {
            return resourceManageHandler.findOne(referenceClass, uniqueIndex, value);
        }

        @Override
        public List<V> getIndex(String index, Object value) {
            return resourceManageHandler.findByIndex(referenceClass, index, value);
        }

        @Override
        public V get(K k) {
            return resourceManageHandler.findById(referenceClass, k);
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

    public DefaultStorageManage(ResourceManageHandler resourceManageHandler) {
        this.resourceManageHandler = resourceManageHandler;
    }

    @Override
    public boolean containStorage(Class<?> type) {
        return storageMap.containsKey(type);
    }

    public void putStorage(Class<?> type, Storage storage) {
        storageMap.put(type, storage);
    }

}
