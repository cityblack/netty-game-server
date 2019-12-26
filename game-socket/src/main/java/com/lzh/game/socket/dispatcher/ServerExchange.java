package com.lzh.game.socket.dispatcher;

import com.lzh.game.socket.exchange.Request;
import com.lzh.game.socket.exchange.Response;
import com.lzh.game.socket.exchange.session.Session;

public interface ServerExchange {

    Request getRequest();

    Response getResponse();

    Session getSession();
}
