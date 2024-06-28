package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.AsyncResponse;
import com.lzh.game.framework.socket.core.LifeCycle;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.protocol.Request;

/**
 * One to many server
 */
public interface GameClient extends LifeCycle {

    /**
     * @param host
     * @param port
     * @param connectTimeout
     * @return
     */
    Session conn(String host, int port, int connectTimeout);

    void oneWay(Session session, Request request);

    default void oneWay(Session session, int msgId) {
        oneWay(session, msgId, null);
    }

    void oneWay(Session session, int msgId, Object params);

    <T> AsyncResponse<T> request(Session session, Request request, Class<T> returnType);

    default <T>AsyncResponse<T> request(Session session, int cmd, Class<T> returnType) {
        return request(session, cmd, null, returnType);
    }

    <T>AsyncResponse<T> request(Session session, int cmd, Object params, Class<T> returnType);
}
