package com.lzh.game.socket.core;

import com.lzh.game.socket.RemotingCommand;

public interface Process<E extends RemotingCommand> {

    void process(RemoteContext context, E e);
}
