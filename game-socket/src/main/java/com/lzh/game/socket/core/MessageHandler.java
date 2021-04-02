package com.lzh.game.socket.core;


import com.lzh.game.socket.core.session.Session;

public interface MessageHandler {

    void opened(Session session);

    void close(Session session);

    void exceptionCaught(Session session);

    void messageReceived(Session session, Object data);
}
