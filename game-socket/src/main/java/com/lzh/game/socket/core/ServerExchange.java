package com.lzh.game.socket.core;

import com.lzh.game.common.scoket.Request;
import com.lzh.game.common.scoket.Response;
import com.lzh.game.common.scoket.session.Session;

public interface ServerExchange {

    Request getRequest();

    Response getResponse();

    Session getSession();
}
