package com.lzh.game.socket.core.process;

import com.lzh.game.socket.core.session.Session;

public interface ProcessEventListen {

    void event(Session session);
}
