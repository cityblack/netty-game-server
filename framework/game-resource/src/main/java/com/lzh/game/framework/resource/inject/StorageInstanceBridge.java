package com.lzh.game.framework.resource.inject;

import com.lzh.game.framework.resource.storage.Storage;
import com.lzh.game.framework.resource.storage.StorageInstance;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;

/**
 * @author zehong.l
 * @since 2024-08-19 11:28
 **/
class StorageInstanceBridge implements MethodHandler {

    private final Storage storage;

    private final String key;

    public StorageInstanceBridge(Storage<?,?> storage, String key) {
        this.storage = storage;
        this.key = key;
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        var instance = (StorageInstance) storage.get(key);
        return instance.getValue();
    }

}
