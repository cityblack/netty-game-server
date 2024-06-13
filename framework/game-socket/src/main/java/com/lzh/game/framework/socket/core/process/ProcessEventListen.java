package com.lzh.game.framework.socket.core.process;

import com.lzh.game.framework.socket.core.session.Session;

public interface ProcessEventListen {

    void event(Session session);
}
