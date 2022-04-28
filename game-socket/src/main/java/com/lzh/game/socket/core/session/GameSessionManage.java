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
public class GameSessionManage<S extends Session> implements SessionManage<S> {

    private List<Consumer<S>> closeListener = new CopyOnWriteArrayList<>();

    private SessionMemoryCacheManage<String, S> cacheManage;

    private SessionFactory<S> sessionFactory;

    public GameSessionManage(SessionMemoryCacheManage<String, S> cacheManage, SessionFactory<S> sessionFactory) {
        this.cacheManage = cacheManage;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public S getSession(String sessionId) {
        S session = getCache().get(sessionId);
        return session;
    }

    @Override
    public void addSessionCloseListening(Consumer<S> consumer) {
        closeListener.add(consumer);
    }

    @Override
    public boolean contain(String sessionId) {
        return Objects.nonNull(getCache().get(sessionId));
    }

    @Override
    public S getSession(Channel channel) {
        return getSession(channel.id().asLongText());
    }

    @Override
    public boolean removeSession(String sessionId) {
        return Objects.nonNull(getCache().remove(sessionId));
    }

    @Override
    public void updateLastAccessTime(S session) {
        session.updateLastAccessTime();
    }

    @Override
    public void pushSession(String sessionId, S session) {
        getCache().put(sessionId, session);
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

    public SessionFactory<S> getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory<S> sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public S createSession(Channel channel) {
        return sessionFactory.createSession(channel);
    }
}
