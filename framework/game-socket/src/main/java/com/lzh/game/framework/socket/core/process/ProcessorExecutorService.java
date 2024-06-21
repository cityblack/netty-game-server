package com.lzh.game.framework.socket.core.process;

import com.lzh.game.framework.socket.core.session.Session;

public interface ProcessorExecutorService {

    void submit(Session session, Object msg, Runnable runnable);

}
