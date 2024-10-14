package com.lzh.game.framework.socket.core.session;

import com.lzh.game.framework.socket.core.session.contain.ConcurrentHashSessionContain;
import com.lzh.game.framework.socket.core.session.contain.SessionContain;
import com.lzh.game.framework.socket.core.session.impl.GameSession;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Slf4j
public class DefaultSessionManage<S extends Session>
        implements SessionManage<S> {
    private SessionContain<String, S> contain;

    private final List<Consumer<S>> closeListener = new CopyOnWriteArrayList<>();

    private final List<Consumer<S>> connectListener = new CopyOnWriteArrayList<>();

    public DefaultSessionManage(SessionContain<String, S> contain) {
        this.contain = contain;
    }

    public DefaultSessionManage() {
        this(new ConcurrentHashSessionContain<>());
    }

    @Override
    public void addCloseListening(Consumer<S> consumer) {
        closeListener.add(consumer);
    }

    @Override
    public void addConnectListener(Consumer<S> connected) {
        connectListener.add(connected);
    }

    @Override
    public void updateLastAccessTime(S session) {
        session.updateLastAccessTime();
    }

    @Override
    public void pushSession(String sessionId, S session) {
        getContain().put(sessionId, session);
        doConnect(session);
    }

    @Override
    public boolean removeSession(String sessionId) {
        S session = getContain().remove(sessionId);
        if (Objects.isNull(session)) {
            return false;
        }
        doClose(session);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S createSession(Channel channel) {
        return (S) GameSession.of(channel);
    }

    protected void doConnect(S session) {
        for (Consumer<S> consumer : this.connectListener) {
            try {
                consumer.accept(session);
            } catch (Exception e) {
                log.error("Session connect event error: ", e);
            }
        }
    }

    protected void doClose(S session) {
        for (Consumer<S> consumer : this.closeListener) {
            try {
                consumer.accept(session);
            } catch (Exception e) {
                log.error("Session close event error: ", e);
            }
        }
    }

    @Override
    public void shutdown() {
        this.closeListener.clear();
        this.connectListener.clear();
        for (S s : getContain().getAll()) {
            s.close();
        }
    }

    @Override
    public S getSession(String sessionId) {
        return getContain().get(sessionId);
    }

    @Override
    public boolean contain(String sessionId) {
        return Objects.nonNull(getContain().get(sessionId));
    }

    @Override
    public List<S> getAllSession() {
        return getContain().getAll();
    }

    public SessionContain<String, S> getContain() {
        return contain;
    }

    public void setContain(SessionContain<String, S> contain) {
        this.contain = contain;
    }
}
