package com.lzh.game.framework.gateway.select;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;

@FunctionalInterface
public interface SessionSelect {

    /**
     * Select session
     * @param request current request
     * @return may be null
     */
    Session selected(Request request);
}
