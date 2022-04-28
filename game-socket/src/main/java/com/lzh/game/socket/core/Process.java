package com.lzh.game.socket.core;

import com.lzh.game.socket.RemotingCmd;

public interface Process<E extends RemotingCmd> {

    void process(E e);
}
