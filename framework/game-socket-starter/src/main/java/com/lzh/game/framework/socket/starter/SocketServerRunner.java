package com.lzh.game.framework.socket.starter;

import com.lzh.game.framework.socket.core.bootstrap.server.ServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.AbstractServerBootstrap;

/**
 * Call back when server init.
 */
public interface SocketServerRunner {

    void run(AbstractServerBootstrap<? extends ServerSocketProperties> server);
}
