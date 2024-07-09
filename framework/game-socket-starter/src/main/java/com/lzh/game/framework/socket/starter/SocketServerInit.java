package com.lzh.game.framework.socket.starter;

import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.AbstractServerBootstrap;

/**
 * Impl the interface to change server config when server init.
 */
public interface SocketServerInit {

    void doInit(GameServerSocketProperties config, AbstractServerBootstrap<? extends GameServerSocketProperties> server);
}
