package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.LifeCycle;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;

/**
 * One to many server
 */
public interface GameClient extends LifeCycle {

    Session conn(String host, int port, int connectTimeout);

    void oneWay(Session session, Request request);

    default void oneWay(Session session, short msgId) {
        oneWay(session, msgId, null);
    }

    /**
     * @param session
     * @param msgId   -- msg id
     */
    void oneWay(Session session, short msgId, Object param);

    <T>AsyncResponse<T> request(Session session, Request request);

    default <T>AsyncResponse<T> request(Session session, short msgId) {
        return request(session, msgId, null);
    }

    void oneWayCompose(Session session, short msgId, Object... params);

    /**
     * @param session
     * @param msgId      -- msg id
     * @param param     -- @protocol
     * @return
     */
    <T>AsyncResponse<T> request(Session session, short msgId, Object param);

    <T>AsyncResponse<T> requestCompose(Session session, short msgId, Object... params);

}
