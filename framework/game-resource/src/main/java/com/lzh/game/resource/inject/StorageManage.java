package com.lzh.game.resource.inject;


import com.lzh.game.resource.Storage;

public interface StorageManage {

    Storage<?, ?> getStorage(Class<?> clazz);

    boolean containStorage(Class<?> clazz);
}
