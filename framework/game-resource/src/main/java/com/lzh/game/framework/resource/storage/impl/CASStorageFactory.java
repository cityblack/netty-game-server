package com.lzh.game.framework.resource.storage.impl;

import com.lzh.game.framework.resource.data.meta.ResourceMeta;
import com.lzh.game.framework.resource.storage.IntKeyStorage;
import com.lzh.game.framework.resource.storage.LongKeyStorage;
import com.lzh.game.framework.resource.storage.Storage;
import com.lzh.game.framework.resource.storage.StorageFactory;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * @author zehong.l
 * @since 2024-08-20 14:20
 **/
public class CASStorageFactory implements StorageFactory {

    private StorageFactory factory;

    public CASStorageFactory(StorageFactory factory) {
        this.factory = factory;
    }

    @Override
    public <K extends Serializable, V> Storage<K, V> createStorage(ResourceMeta<V> meta) {
        var type = meta.getId().getType();
        Storage storage = factory.createStorage(meta);
        if (type == int.class || type == Integer.class) {
            return new CASIntKeyStorage<>(storage);
        } else if (type == long.class || type == Long.class) {
            return new CASLongKeyStorage<>(storage);
        }
        return new CASStorage<>(storage);
    }

    public static class CASStorage<K extends Serializable, V> implements Storage<K, V> {

        protected final AtomicBoolean status ;

        protected final Storage<K, V> storage;

        public CASStorage(Storage<K, V> storage) {
            this.storage = storage;
            this.status = new AtomicBoolean();
        }

        @Override
        public V get(K k) {
            checkWait();
            return storage.get(k);
        }

        @Override
        public V getOrThrow(K k) {
            return storage.getOrThrow(k);
        }

        @Override
        public void reload() {
            if (status.compareAndSet(false, true)) {
                try {
                    storage.reload();
                } finally {
                    status.set(false);
                }
            }
        }

        @Override
        public List<V> getAll() {
            checkWait();
            return storage.getAll();
        }

        @Override
        public List<V> getIndex(String indexName, Serializable value) {
            checkWait();
            return storage.getIndex(indexName, value);
        }

        @Override
        public V getUnique(String UniqueIndexName, Serializable value) {
            checkWait();
            return storage.getUnique(UniqueIndexName, value);
        }

        protected void checkWait() {
            while (status.get()) {
                LockSupport.parkNanos(1000L);
            }
        }

        public void setWait() {
            status.set(true);
        }

        public void freeWait() {
            status.set(false);
        }

        public Storage<K, V> getStorage() {
            return storage;
        }
    }

    static class CASIntKeyStorage<V> extends CASStorage<Integer, V> implements IntKeyStorage<V> {

        public CASIntKeyStorage(Storage<Integer, V> storage) {
            super(storage);
        }

        @Override
        public V get(int id) {
            checkWait();
            IntKeyStorage<V> storage = (IntKeyStorage<V>) this.storage;
            return storage.get(id);
        }

    }

    static class CASLongKeyStorage<V> extends CASStorage<Long, V> implements LongKeyStorage<V> {

        public CASLongKeyStorage(Storage<Long, V> storage) {
            super(storage);
        }

        @Override
        public V get(long id) {
            checkWait();
            LongKeyStorage<V> keyStorage = (LongKeyStorage<V>) this.storage;
            return keyStorage.get(id);
        }
    }
}
