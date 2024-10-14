package com.lzh.game.framework.socket.core.session.contain;

import com.lzh.game.framework.socket.core.session.Session;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSessionContain<T extends Session> implements SessionContain<String, T> {

    private Map<String, T> cache = new ConcurrentHashMap<>();

    @Override
    public void put(String key, T value) {
        cache.put(getKey(key), value);
    }

    @Override
    public T get(String key) {
        return cache.get(getKey(key));
    }

    @Override
    public boolean containKey(String key) {
        return cache.containsKey(getKey(key));
    }

    @Override
    public T remove(String key) {
        return cache.remove(getKey(key));
    }

    @Override
    public String getKey(String key) {
        return SESSION_PREFIX + key;
    }

    @Override
    public void clean() {
        cache.forEach((k, v) -> v.close());
        this.cache = null;
    }

    @Override
    public List<T> getAll() {
        return List.copyOf(cache.values());
    }

}
