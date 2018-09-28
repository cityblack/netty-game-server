package com.lzh.netty.socket.protocol.session.cache;

import com.lzh.netty.socket.protocol.session.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

@Slf4j
public class GameSessionMemoryCacheManage<T extends Session> implements SessionMemoryCacheManage<T>, InitializingBean {

    private SessionMemoryCache<String,T> cache;

    @Override
    public void put(String key, T value) {
        Assert.notNull(value,"session can't not null");
        cache.put(key,value);
    }

    @Override
    public T remove(String key) {
        return cache.remove(key);
    }

    @Override
    public T get(String key) {
        return cache.get(key);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (cache != null && cache instanceof DefaultSessionMemoryCache) {
            log.info("Use default session memoryCache");
        }
    }

    public void setCache(SessionMemoryCache<String, T> cache) {
        this.cache = cache;
    }
}
