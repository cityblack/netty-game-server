/*
package com.lzh.game.start.util.lock;

import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RedisLockGet<T> implements LockGet<T> {

    private Supplier<T> supplier;

    private String key;

    private RedissonClient client;

    public RedisLockGet(String key, Supplier<T> supplier, RedissonClient client) {
        this.key = key;
        this.supplier = supplier;
        this.client = client;
    }

    @Override
    public T lockAndGet(long time) {
        lock(time);
        return supplier.get();
    }

    @Override
    public void unlock() {
        client.getLock(key).unlock();
    }

    @Override
    public T lockAndGet() {
        client.getLock(key);
        return supplier.get();
    }

    private void lock(long time) {
        client.getLock(key).lock(time, TimeUnit.MILLISECONDS);
    }
}
*/
