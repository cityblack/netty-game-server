package com.lzh.game.socket.core.session;

import com.lzh.game.socket.core.session.cache.SessionMemoryCache;
import com.lzh.game.socket.core.session.cache.SessionMemoryCacheManage;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Slf4j
public class GameSessionManage<S extends Session> extends AbstractSessionManage<S> {

    private SessionMemoryCacheManage<String, S> cacheManage;

    public GameSessionManage(SessionFactory<S> factory, SessionMemoryCacheManage<String, S> cacheManage) {
        super(factory);
        this.cacheManage = cacheManage;
    }

    @Override
    public S getSession(String sessionId) {
        S session = getCache().get(sessionId);
        return session;
    }

    @Override
    public boolean contain(String sessionId) {
        return Objects.nonNull(getCache().get(sessionId));
    }

    @Override
    public List<S> getAllSession() {
        return getCache().getAll();
    }

    @Override
    protected void doPutSession(String sessionId, S session) {
        getCache().put(sessionId, session);
    }

    @Override
    protected S doRemoveSession(String sessionId) {
        return getCache().remove(sessionId);
    }

    public SessionMemoryCacheManage<String, S> getCacheManage() {
        return cacheManage;
    }

    public SessionMemoryCache<String, S> getCache() {
        return this.cacheManage.getSessionCache();
    }

    public void setCacheManage(SessionMemoryCacheManage<String, S> cacheManage) {
        this.cacheManage = cacheManage;
    }
}
