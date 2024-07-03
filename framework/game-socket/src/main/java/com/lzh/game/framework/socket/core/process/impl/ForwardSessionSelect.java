package com.lzh.game.framework.socket.core.process.impl;

import com.lzh.game.framework.socket.core.bootstrap.client.GameClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.client.GameTcpClient;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.protocol.Request;

@FunctionalInterface
public interface ForwardSessionSelect {

    Session selected(GameTcpClient<GameClientSocketProperties> client, Request request);
}
