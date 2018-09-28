package com.lzh.netty.socket.protocol.session.cache;

import com.lzh.netty.socket.protocol.session.Session;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DefaultSessionMemoryCache<T extends Session> implements SessionMemoryCache<String,T>, DisposableBean {

    private Map<String,T> cache = new ConcurrentHashMap<>();

    @Override
    public void put(String key, T value) {

        cache.put(getKey(key),value);
    }

    @Override
    public void put(String key, T value, int time, TimeUnit unit) {

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
        cache.forEach((k,v) -> v.close());
        this.cache = null;
    }

    @Override
    public void destroy() throws Exception {
        clean();
    }
}
