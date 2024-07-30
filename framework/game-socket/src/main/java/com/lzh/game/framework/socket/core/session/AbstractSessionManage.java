package com.lzh.game.framework.socket.core.session;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class AbstractSessionManage<S extends Session>
        implements SessionManage<S> {

    private List<Consumer<S>> closeListener = new CopyOnWriteArrayList<>();

    private List<Consumer<S>> connectListener = new CopyOnWriteArrayList<>();

    private SessionFactory<S> factory;

    public AbstractSessionManage(SessionFactory<S> factory) {
        this.factory = factory;
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
        doPutSession(sessionId, session);
        doConnect(session);
    }

    @Override
    public boolean removeSession(String sessionId) {
        S session = doRemoveSession(sessionId);
        if (Objects.isNull(session)) {
            return false;
        }
        doClose(session);
        return true;
    }

    @Override
    public S createSession(Channel channel) {
        return factory.createSession(channel);
    }

    protected void doConnect(S session) {
        for (Consumer<S> consumer : this.connectListener) {
            consumer.accept(session);
        }
    }

    protected void doClose(S session) {
        for (Consumer<S> consumer : this.closeListener) {
            consumer.accept(session);
        }
    }

    protected abstract void doPutSession(String sessionId, S session);

    protected abstract S doRemoveSession(String sessionId);
}
