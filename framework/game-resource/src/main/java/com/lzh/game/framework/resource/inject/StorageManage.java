package com.lzh.game.framework.resource.inject;


import com.lzh.game.framework.resource.Storage;

public interface StorageManage {

    Storage<?, ?> getStorage(Class<?> clazz);

    boolean containStorage(Class<?> clazz);
}
