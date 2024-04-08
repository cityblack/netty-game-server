package com.lzh.game.socket.core.process;

import com.lzh.game.socket.core.AbstractRemotingCommand;

/**
 * Io read data process
 * Request
 * Response
 * forward -- gateway
 *
 * @param <E>
 */
public interface Process<E extends AbstractRemotingCommand> {

    void process(E e);
}
