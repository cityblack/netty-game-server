package com.lzh.netty.socket.dispatcher;

import com.lzh.netty.socket.protocol.Request;
import com.lzh.netty.socket.protocol.Response;
import com.lzh.netty.socket.protocol.session.Session;

public interface ServerExchange {

    Request getRequest();

    Response getResponse();

    Session getSession();
}
