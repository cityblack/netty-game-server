package com.lzh.game.framework.socket.core.process;

import com.lzh.game.framework.socket.core.session.Session;

public interface MessageHandler {

    void opened(Session session);

    void close(Session session);

    void exceptionCaught(Session session, Throwable throwable);

    void messageReceived(Session session, Object data);
}
