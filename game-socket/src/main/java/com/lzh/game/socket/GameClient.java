package com.lzh.game.socket;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.core.LifeCycle;
import com.lzh.game.socket.core.session.Session;

import java.util.concurrent.CompletableFuture;

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
    Session conn(String host, int port, long connectTimeout);

    void oneWay(Session session, Request request);

    default void oneWay(Session session, int cmd, Object params) {
        oneWay(session, Constant.REQUEST_COMMAND_KEY, cmd, params);
    }

    default void oneWay(Session session, int cmd) {
        oneWay(session, Constant.REQUEST_COMMAND_KEY, cmd, null);
    }

    void oneWay(Session session, int commandKey, int cmd, Object params);

    <T>CompletableFuture<T> request(Session session, Request request, Class<T> clazz);

    default <T>CompletableFuture<T> request(Session session, int cmd, Class<T> clazz) {
        return request(session, cmd, null, clazz);
    }

    default <T>CompletableFuture<T> request(Session session, int cmd, Object params, Class<T> clazz) {
        return request(session, Constant.REQUEST_COMMAND_KEY, cmd, params, clazz);
    }

    <T>CompletableFuture<T> request(Session session, int commandKey, int cmd, Object params, Class<T> clazz);
}
