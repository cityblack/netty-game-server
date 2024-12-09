package com.lzh.game.framework.socket.starter;

import com.lzh.game.framework.socket.core.bootstrap.server.ServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerBootstrap;

/**
 * Call back when server init.
 */
public interface SocketServerRunner {

    void run(ServerBootstrap<? extends ServerSocketProperties> server);
}
