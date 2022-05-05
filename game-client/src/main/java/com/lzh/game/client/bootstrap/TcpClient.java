package com.lzh.game.client.bootstrap;

import com.lzh.game.socket.core.session.Session;

/**
 * One to many server
 */
public interface TcpClient {

    /**
     * @param host
     * @param port
     * @param connectTimeout
     * @return
     */
    Session conn(String host, int port, long connectTimeout);
}
