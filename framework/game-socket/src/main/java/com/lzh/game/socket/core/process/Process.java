package com.lzh.game.socket.core.process;

import com.lzh.game.socket.RemotingCommand;

/**
 * Io read data process
 *  Request
 *  Response
 *  forward -- gateway
 * @param <E>
 */
public interface Process<E extends RemotingCommand> {

    void process(RemoteContext context, E e);
}
