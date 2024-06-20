package com.lzh.game.framework.socket.core.process.event;

import com.lzh.game.framework.socket.core.session.Session;

public interface ProcessEventListen {

    void event(Session session);
}
