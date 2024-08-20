package com.lzh.game.framework.resource.storage.manager;


import com.lzh.game.framework.resource.storage.Storage;

import java.util.List;

public interface StorageManager {

    Storage<?, ?> getStorage(Class<?> clazz);

    boolean containStorage(Class<?> clazz);

    void registerStore(Class<?> clz, Storage<?, ?> storage);

    void reload(List<Class<?>> clz);
}
