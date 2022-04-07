package com.lzh.game.client.bootstrap;

import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.common.scoket.session.SessionManage;

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

    SessionManage<ClientGameSession> sessionManage();
}
