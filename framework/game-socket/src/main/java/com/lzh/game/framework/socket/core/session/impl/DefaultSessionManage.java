package com.lzh.game.framework.socket.core.session.impl;

import com.lzh.game.framework.common.utils.Pair;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionEvent;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.impl.GameSession;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

@Slf4j
public class DefaultSessionManage<S extends Session>
        implements SessionManage<S> {
    private final Map<String, S> sessions;
    private final Map<SessionEvent, List<Pair<String, BiConsumer<S, Object>>>> events;

    public DefaultSessionManage() {
        events = new EnumMap<>(SessionEvent.class);
        for (SessionEvent value : SessionEvent.values()) {
            events.put(value, new CopyOnWriteArrayList<>());
        }
        sessions = new ConcurrentHashMap<>();
    }

    @Override
    public void updateLastAccessTime(S session) {
        session.updateLastAccessTime();
    }

    @Override
    public void pushSession(String sessionId, S session) {
        sessions.put(sessionId, session);
        doConnect(session);
    }

    @Override
    public boolean removeSession(String sessionId) {
        S session = sessions.remove(sessionId);
        if (Objects.isNull(session)) {
            return false;
        }
        doClose(session);
        return true;
    }

    @Override
    public void addListener(SessionEvent event, String key, BiConsumer<S, Object> call) {
        removeListener(event, key);
        var list = events.get(event);
        list.add(Pair.of(key, call));
    }

    @Override
    public void removeListener(SessionEvent event, String key) {
        var list = events.get(event);
        for (var it = list.iterator(); it.hasNext();) {
            var p = it.next();
            if (Objects.equals(p.getFirst(), key)) {
                it.remove();
                return;
            }
        }
    }

    @Override
    public void pushEvent(SessionEvent event, S session, Object data) {
        var list = events.get(event);
        for (var pair : list) {
            try {
                pair.getSecond().accept(session, data);
            } catch (Exception e) {
                log.error("execute session [{}] event [{}] error", session.getId(), event);
            }
        }
    }

    protected void doConnect(S session) {
        pushEvent(SessionEvent.CONNECT, session, null);
    }

    protected void doClose(S session) {
        pushEvent(SessionEvent.CLOSE, session, null);
    }

    @Override
    public void shutdown() {
        this.events.clear();
        for (S s : sessions.values()) {
            s.close();
        }
    }

    @Override
    public S getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public boolean contain(String sessionId) {
        return Objects.nonNull(sessions.get(sessionId));
    }

    @Override
    public List<S> getAllSession() {
        return List.copyOf(sessions.values());
    }

    @Override
    @SuppressWarnings("unchecked")
    public S createSession(Channel channel, BootstrapContext<?> context) {
        return (S) GameSession.of(channel, context);
    }
}
