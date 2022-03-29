package com.lzh.game.common.scoket;

import com.lzh.game.common.scoket.session.Session;

public interface MessageHandler {

    void opened(Session session);

    void close(Session session);

    void exceptionCaught(Session session);

    void messageReceived(Session session, Object data);
}
