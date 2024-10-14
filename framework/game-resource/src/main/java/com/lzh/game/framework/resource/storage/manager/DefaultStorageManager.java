package com.lzh.game.framework.resource.storage.manager;


import com.lzh.game.framework.resource.data.meta.ResourceMeta;
import com.lzh.game.framework.resource.storage.Storage;
import com.lzh.game.framework.resource.storage.StorageFactory;
import com.lzh.game.framework.resource.storage.impl.CasStorageFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultStorageManager implements StorageManager {

    private final Map<Class<?>, Storage<?,?>> storages = new ConcurrentHashMap<>();

    private final StorageFactory storageFactory;

    public DefaultStorageManager(StorageFactory storageFactory) {
        this.storageFactory = storageFactory;
    }

    @Override
    public Storage<?, ?> getStorage(Class<?> clazz) {
        return storages.get(clazz);
    }

    protected Storage<?, ?> createStorage(ResourceMeta<?> meta) {
        return storageFactory.createStorage(meta);
    }

    @Override
    public boolean containStorage(Class<?> type) {
        return storages.containsKey(type);
    }

    @Override
    public void registerStore(Class<?> clz, Storage<?, ?> storage) {
        storages.put(clz, storage);
    }

    @Override
    public void reload(List<Class<?>> clz) {
        var storages = checkClass(clz);
        try {
            for (Storage<?, ?> storage : storages) {
                if (storage instanceof CasStorageFactory.CASStorage<?,?> cas) {
                    cas.setWait();
                }
            }
            for (Storage<?, ?> storage : storages) {
                if (storage instanceof CasStorageFactory.CASStorage<?,?> cas) {
                    cas.getStorage().reload();
                } else {
                    storage.reload();
                }
            }
        } finally {
            for (Storage<?, ?> storage : storages) {
                if (storage instanceof CasStorageFactory.CASStorage<?,?> cas) {
                    cas.freeWait();
                }
            }
        }
    }

    private List<Storage<?,?>> checkClass(List<Class<?>> clz) {
        var set = new HashSet<>(clz);
        List<Storage<?,?>> list = new ArrayList<>(clz.size());
        for (Class<?> c : clz) {
            if (set.contains(c)) {
                continue;
            }
            set.add(c);
            if (!containStorage(c)) {
                throw new IllegalArgumentException("Not defined " + c.getName() + " storage.");
            }
            list.add(storages.get(c));
        }
        return list;
    }
}
