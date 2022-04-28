package com.lzh.game.socket;

import com.lzh.game.socket.core.LifeCycle;

public interface GameServer extends LifeCycle {

    void asyncStart();

    int getPort();
}
