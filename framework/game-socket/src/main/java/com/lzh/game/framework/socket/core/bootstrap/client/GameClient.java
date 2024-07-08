package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.bootstrap.LifeCycle;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;

/**
 * One to many server
 */
public interface GameClient extends LifeCycle {

    Session conn(String host, int port, int connectTimeout);

    /**
     * @param session
     * @param param   -- @Protocol class
     */
    void oneWay(Session session, Object param);


    void oneWayRequest(Session session, Request request);


    <T> AsyncResponse<T> request(Session session, Request request, Class<T> type);

    /**
     * @param session
     * @param param   -- @protocol
     * @return
     */
    <T> AsyncResponse<T> request(Session session, Object param, Class<T> type);

    /**
     * @param session
     * @param msgId
     * @param params  -- Can be null
     */
    void oneWayCompose(Session session, short msgId, Object... params);


    <T> AsyncResponse<T> requestCompose(Session session, short msgId, Class<T> type, Object... params);

}
