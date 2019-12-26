package com.lzh.game.socket.exchange.session.manage;

import com.lzh.game.socket.exchange.session.*;
import com.lzh.game.socket.exchange.session.cache.SessionMemoryCacheManage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Slf4j
public class GameSessionManage implements SessionManage<GameSession> {

    private List<Consumer<Session>> closeListener = new CopyOnWriteArrayList<>();

    private SessionMemoryCacheManage<GameSession> cacheManage;

    @Override
    public GameSession getSession(String sessionId) {
        GameSession session = getSessionMemoryCache().get(sessionId);
        checkSession(session);
        return session;
    }

    @Override
    public GameSession closeSession(String sessionId) {
        GameSession session = getSessionMemoryCache().remove(sessionId);
        if (Objects.isNull(session)) {
            return null;
        }

        session.close(new SessionCloseListener<Session>() {
            @Override
            public void onSuccess(Session session) {
                closeListener.forEach(e -> e.accept(session));
            }

            @Override
            public void onFail(Session session, Throwable throwable) {
                log.error("", throwable);
            }

            @Override
            public void onComplete(Session session) {

            }
        });

        return session;
    }

    private void checkSession(GameSession session) {
        Objects.requireNonNull(session,"The session is null.");
        if (!session.opened()) {
            throw new RuntimeException("The session is closed.");
        }
    }

    @Override
    public void pushSession(String sessionId, GameSession session) {
        getSessionMemoryCache().put(sessionId,session);
    }

    @Override
    public void addSessionCloseListening(Consumer<Session> consumer) {
        closeListener.add(consumer);
    }

    public SessionMemoryCacheManage<GameSession> getSessionMemoryCache() {
        return cacheManage;
    }

    public void setCacheManage(SessionMemoryCacheManage<GameSession> cacheManage) {
        this.cacheManage = cacheManage;
    }

    @Override
    public void updateLastAccessTime(GameSession session) {
        if (session instanceof DefaultGameSession) {
            ((DefaultGameSession)session).setLastAccessTime();
        }
    }
}
