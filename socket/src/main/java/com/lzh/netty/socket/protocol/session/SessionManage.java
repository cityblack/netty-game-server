package com.lzh.netty.socket.protocol.session;


import io.netty.channel.Channel;

/**
 * Main class for for access to the {@link Session,Channel}
 */
public interface SessionManage<T> {

    T getSession(String sessionId);

    T closeSession(String sessionId);

    void updateLastAccessTime(T session);
}
