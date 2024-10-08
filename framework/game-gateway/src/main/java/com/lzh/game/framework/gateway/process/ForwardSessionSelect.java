package com.lzh.game.framework.gateway.process;

import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.protocol.Request;

@FunctionalInterface
public interface ForwardSessionSelect {

    Session selected(Request request);
}
