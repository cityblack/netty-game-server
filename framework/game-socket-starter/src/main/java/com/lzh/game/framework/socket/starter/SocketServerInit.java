package com.lzh.game.framework.socket.starter;

import com.lzh.game.framework.socket.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.AbstractServerBootstrap;

/**
 * Impl the interface to change server config when server init.
 */
public interface SocketServerInit {

    void doInit(GameServerSocketProperties config, AbstractServerBootstrap server);
}
