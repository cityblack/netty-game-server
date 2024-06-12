package com.lzh.socket.starter;

import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.core.bootstrap.AbstractServerBootstrap;

/**
 * Impl the interface to change server config when server init.
 */
public interface SocketServerInit {

    void doInit(GameServerSocketProperties config, AbstractServerBootstrap server);
}
