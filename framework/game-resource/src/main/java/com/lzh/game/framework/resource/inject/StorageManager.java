package com.lzh.game.framework.resource.inject;


import com.lzh.game.framework.resource.storage.Storage;

public interface StorageManager {

    Storage<?, ?> getStorage(Class<?> clazz);

    boolean containStorage(Class<?> clazz);

    void registerStore(Class<?> clz, Storage<?, ?> storage);
}
