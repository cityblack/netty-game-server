package com.lzh.game.framework.socket.core.process;

import com.lzh.game.framework.socket.core.session.Session;

/**
 * Io read data process
 * Request
 * Response
 * forward -- gateway
 *
 * @param <E>
 */
public interface Processor<E> {

    void process(Session session, E e);

    boolean match(E msg);
}
