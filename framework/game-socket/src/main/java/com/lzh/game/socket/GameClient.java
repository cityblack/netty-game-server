package com.lzh.game.socket;

import com.lzh.game.socket.core.AsyncResponse;
import com.lzh.game.socket.core.LifeCycle;
import com.lzh.game.socket.core.session.Session;

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

    default void oneWay(Session session, int cmd) {
        oneWay(session, cmd, null);
    }

    void oneWay(Session session, int cmd, Object params);

    <T> AsyncResponse<T> request(Session session, Request request, Class<T> returnType);

    default <T>AsyncResponse<T> request(Session session, int cmd, Class<T> returnType) {
        return request(session, cmd, null, returnType);
    }

    <T>AsyncResponse<T> request(Session session, int cmd, Object params, Class<T> returnType);
}
