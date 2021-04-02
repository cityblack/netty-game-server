package com.lzh.game.socket.core;

import com.lzh.game.socket.core.session.Session;

public interface ServerExchange {

    Request getRequest();

    Response getResponse();

    Session getSession();
}
