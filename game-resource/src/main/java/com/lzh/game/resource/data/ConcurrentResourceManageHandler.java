package com.lzh.game.resource.data;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentResourceManageHandler implements ResourceManageHandler {
    // Fact option handler
    private ResourceManageHandler handler;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ConcurrentResourceManageHandler(ResourceManageHandler handler) {
        this.handler = handler;
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        getReadLock().lock();
        try {
            return handler.findAll(clazz);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public <T> List<T> findByIndex(Class<T> clazz, String index, Object value) {
        getReadLock().lock();
        try {
            return handler.findByIndex(clazz, index, value);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public <T> T findOne(Class<T> clazz, String uniqueIndex, Object value) {
        getReadLock().lock();
        try {
            return handler.findOne(clazz, uniqueIndex, value);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public <T, K> T findById(Class<T> clazz, K k) {
        getReadLock().lock();
        try {
            return handler.findById(clazz, k);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public void reload() {
        getWriteLock().lock();
        try {
            handler.reload();
        } finally {
            getWriteLock().unlock();
        }
    }

    @Override
    public void reload(Class<?> clazz) {
        getWriteLock().lock();
        try {
            handler.reload(clazz);
        } finally {
            getWriteLock().unlock();
        }
    }

    private Lock getReadLock() {
        return lock.readLock();
    }

    private Lock getWriteLock() {
        return lock.writeLock();
    }
}
