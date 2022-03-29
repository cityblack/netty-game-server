package com.lzh.game.socket.core;

import com.lzh.game.common.scoket.session.Session;

public interface RequestBusinessPool {

    void submit(ServerExchange exchange, Runnable runnable);

    void submit(Session session, Runnable runnable);
}
