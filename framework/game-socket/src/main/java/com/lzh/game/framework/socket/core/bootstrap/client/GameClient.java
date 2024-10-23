package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.bootstrap.LifeCycle;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;

/**
 * One to many server
 */
public interface GameClient extends LifeCycle {

    Session conn(String host, int port, int connectTimeout);

}
