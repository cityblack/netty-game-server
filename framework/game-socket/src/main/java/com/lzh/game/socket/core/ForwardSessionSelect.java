package com.lzh.game.socket.core;

import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.session.Session;

@FunctionalInterface
public interface ForwardSessionSelect {

    Session selected(GameTcpClient client, Request request);
}
