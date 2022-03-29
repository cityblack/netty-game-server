package com.lzh.game.client.bootstrap;

import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.common.scoket.session.SessionManage;

/**
 * One to many server
 */
public interface TcpClient {

    Session conn(String host, int port);

    SessionManage<ClientGameSession> sessionManage();
}
