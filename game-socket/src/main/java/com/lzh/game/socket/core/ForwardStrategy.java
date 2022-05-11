package com.lzh.game.socket.core;

import com.lzh.game.socket.GameRequest;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.session.Session;

@FunctionalInterface
public interface ForwardStrategy {

    Session selected(GameTcpClient client, GameRequest request);
}
