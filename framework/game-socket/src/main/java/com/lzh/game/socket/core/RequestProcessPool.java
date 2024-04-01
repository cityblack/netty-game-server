package com.lzh.game.socket.core;

import com.lzh.game.socket.core.session.Session;

public interface RequestProcessPool {

    void submit(RemoteContext context, Runnable runnable);

    void submit(Session session, Runnable runnable);
}
