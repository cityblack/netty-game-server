package com.lzh.game.start.util.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.function.Supplier;

@Component
public class LockUtils {

    @Autowired
    private RedissonClient client;

    /**
     * @return
     */
    public <T>LockGet<T> getLock(String key, Supplier<T> supplier) {
        return new RedisLockGet(key, supplier, client);
    }

    public RLock getLock(String key) {
        return client.getLock(key);
    }

    public RLock getLock(Serializable key, Class<?> clazz) {
        String pre = String.join(":", clazz.getSimpleName(), key.toString());
        return getLock(pre);
    }

}
