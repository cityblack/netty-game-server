package com.lzh.game.framework.socket.starter;

import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.AbstractServerBootstrap;

/**
 * Call back when server start.
 */
public interface SocketServerRunner {

    void run(GameServerSocketProperties config, AbstractServerBootstrap<? extends GameServerSocketProperties> server);
}
