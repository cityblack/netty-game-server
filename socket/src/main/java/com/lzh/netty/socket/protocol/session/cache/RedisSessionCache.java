package com.lzh.netty.socket.protocol.session.cache;

import com.lzh.netty.socket.protocol.session.Session;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import java.util.concurrent.TimeUnit;

@Deprecated
public class RedisSessionCache<S extends Session> implements SessionMemoryCache<String,S> {

    private ReactiveRedisTemplate<String,S> template;

    @Override
    public void put(String key, S value) {
        template.opsForValue().set(getKey(key), value);
    }

    @Override
    public void put(String key, S value, int time, TimeUnit unit) {

    }

    @Override
    public S get(String key) {
        return template.opsForValue().get(getKey(key)).block();
    }

    @Override
    public boolean containKey(String key) {
        return false;
    }

    @Override
    public S remove(String key) {
        S value = get(key);
        template.delete(key);
        return value;
    }

    @Override
    public String getKey(String key) {
        return SESSION_PREFIX + key;
    }

    @Override
    public void clean() {

    }

    public void setTemplate(ReactiveRedisTemplate<String, S> template) {
        this.template = template;
    }
}
