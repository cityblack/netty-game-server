package com.lzh.game.socket.core.process;

import com.lzh.game.socket.RemotingCommand;
import com.lzh.game.socket.core.RemoteContext;

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
