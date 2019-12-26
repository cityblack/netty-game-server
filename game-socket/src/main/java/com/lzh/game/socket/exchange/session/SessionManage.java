package com.lzh.game.socket.exchange.session;


import io.netty.channel.Channel;

import java.util.function.Consumer;

/**
 * Main class for for access to the {@link Session,Channel}
 */
public interface SessionManage<T> {

    T getSession(String sessionId);

    T closeSession(String sessionId);

    void updateLastAccessTime(T session);

    void pushSession(String sessionId, T session);

    void addSessionCloseListening(Consumer<Session> consumer);
}
