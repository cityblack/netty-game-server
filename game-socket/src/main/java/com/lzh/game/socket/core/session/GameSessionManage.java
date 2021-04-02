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
public class GameSessionManage implements SessionManage<DefaultGameSession> {

    private List<Consumer<DefaultGameSession>> closeListener = new CopyOnWriteArrayList<>();

    private SessionMemoryCacheManage<String, DefaultGameSession> cacheManage;

    private SessionFactory<DefaultGameSession> sessionFactory;

    public GameSessionManage(SessionMemoryCacheManage<String, DefaultGameSession> cacheManage, SessionFactory<DefaultGameSession> sessionFactory) {
        this.cacheManage = cacheManage;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public DefaultGameSession getSession(String sessionId) {
        DefaultGameSession session = (DefaultGameSession) getCache().get(sessionId);
        return session;
    }

    @Override
    public DefaultGameSession closeSession(String sessionId) {
        DefaultGameSession session = (DefaultGameSession) getCache().remove(sessionId);
        if (Objects.isNull(session)) {
            return null;
        }
        session.close();
        return session;
    }

    private void checkSession(Session session) {
        Objects.requireNonNull(session, "The session is null.");
        if (!session.opened()) {
            throw new RuntimeException("The session is closed.");
        }
    }

    @Override
    public void addSessionCloseListening(Consumer<DefaultGameSession> consumer) {
        closeListener.add(consumer);
    }

    @Override
    public boolean contain(String sessionId) {
        return Objects.nonNull(getCache().get(sessionId));
    }

    @Override
    public DefaultGameSession getSession(Channel channel) {
        return getSession(channel.id().asLongText());
    }

    @Override
    public void updateLastAccessTime(DefaultGameSession session) {
        session.setLastAccessTime();
    }

    @Override
    public void pushSession(String sessionId, DefaultGameSession session) {
        getCache().put(sessionId, session);
    }

    public SessionMemoryCacheManage<String, DefaultGameSession> getCacheManage() {
        return cacheManage;
    }

    public SessionMemoryCache getCache() {
        return this.cacheManage.getSessionCache();
    }

    public void setCacheManage(SessionMemoryCacheManage<String, DefaultGameSession> cacheManage) {
        this.cacheManage = cacheManage;
    }

    public SessionFactory<DefaultGameSession> getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory<DefaultGameSession> sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public DefaultGameSession createSession(Channel channel) {
        return sessionFactory.createSession(channel);
    }
}
