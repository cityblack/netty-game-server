package com.lzh.game.socket.core.process;

import com.lzh.game.socket.core.session.Session;

public interface ProcessorExecutorService<T> {

    void submit(Session session, T command, Runnable runnable);

}
