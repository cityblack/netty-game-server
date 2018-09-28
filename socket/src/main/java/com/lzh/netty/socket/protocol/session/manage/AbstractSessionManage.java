package com.lzh.netty.socket.protocol.session.manage;

import com.lzh.netty.socket.protocol.session.ChannelSession;
import com.lzh.netty.socket.protocol.session.Session;
import com.lzh.netty.socket.protocol.session.SessionManage;
import com.lzh.netty.socket.protocol.session.cache.SessionMemoryCacheManage;
import io.netty.channel.Channel;

import java.util.Objects;


/**
 * session manage
 * singleton model
 */
public abstract class AbstractSessionManage<T extends Session> implements SessionManage<T>, ChannelSession {

    @Override
    public Session getSession(Channel channel) {
        return getSession(channel.id().asLongText());
    }

    @Override
    public Session closeSession(Channel channel) {
        return closeSession(channel.id().asLongText());
    }

    @Override
    public T getSession(String sessionId) {
        T session = getSessionMemoryCache().get(sessionId);
        checkSession(session);
        return session;
    }

    @Override
    public T closeSession(String sessionId) {
        T session = getSessionMemoryCache().remove(sessionId);
        checkSession(session);
        session.close();
        return session;
    }

    private void checkSession(T session) {
        Objects.requireNonNull(session,"the session is null.");
        if (!session.opened()) {
            throw new RuntimeException("the session is closed.");
        }
    }

    protected void pushSession(String sessionId, T session) {
        getSessionMemoryCache().put(sessionId,session);
    }

    protected AbstractSessionManage() {}

    public abstract SessionMemoryCacheManage<T> getSessionMemoryCache();
}
