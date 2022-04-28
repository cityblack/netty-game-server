package com.lzh.game.socket.core;

import com.lzh.game.socket.Request;
import com.lzh.game.socket.Response;
import com.lzh.game.socket.core.session.Session;

public interface ServerExchange {

    Request getRequest();

    Response getResponse();

    Session getSession();
}
