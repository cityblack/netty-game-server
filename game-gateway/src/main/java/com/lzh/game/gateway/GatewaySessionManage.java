package com.lzh.game.gateway;

import com.lzh.game.socket.core.session.GameSessionManage;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionFactory;
import com.lzh.game.socket.core.session.cache.DefaultSessionMemoryCache;
import com.lzh.game.socket.core.session.cache.SessionMemoryCache;
import com.lzh.game.socket.core.session.cache.SessionMemoryCacheManage;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GatewaySessionManage<S extends Session> extends GameSessionManage<S> {

    public GatewaySessionManage(SessionFactory<S> factory) {
        super(factory, new CacheManage());
    }

    static class CacheManage<S extends Session> implements SessionMemoryCacheManage<String, S> {

        @Override
        public SessionMemoryCache<String, S> getSessionCache() {
            return new Cache();
        }
    }

    static class Cache<S extends Session> extends DefaultSessionMemoryCache<S> {

        private List<S> list = new CopyOnWriteArrayList<>();

        @Override
        public S remove(String key) {
            S session = super.remove(key);
            for (Iterator<S> iterator = list.stream().iterator(); iterator.hasNext();) {
                if (iterator.next().getId().equals(key)) {
                    iterator.remove();
                    break;
                }
            }
            return session;
        }

        @Override
        public void put(String key, S value) {
            super.put(key, value);
            this.list.add(value);
        }

        @Override
        public List<S> getAll() {
            return this.list;
        }
    }
}
