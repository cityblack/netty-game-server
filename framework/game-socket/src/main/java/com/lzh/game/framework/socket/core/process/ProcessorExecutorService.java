package com.lzh.game.framework.socket.core.process;

import com.lzh.game.framework.socket.core.session.Session;

public interface ProcessorExecutorService<T> {

    void submit(Session session, T msg, Runnable runnable);

}
