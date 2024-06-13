package com.lzh.game.framework.socket.core;

import com.lzh.game.framework.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.protocol.Request;

@FunctionalInterface
public interface ForwardSessionSelect {

    Session selected(GameTcpClient client, Request request);
}
