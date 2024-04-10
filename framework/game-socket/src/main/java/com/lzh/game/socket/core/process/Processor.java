package com.lzh.game.socket.core.process;

import com.lzh.game.socket.core.session.Session;

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
}
