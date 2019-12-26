package com.lzh.game.socket.exchange;


import com.lzh.game.socket.exchange.request.RequestHeader;
import com.lzh.game.socket.exchange.session.Session;

public interface Request<T> {

    Session getSession();

    int port();

    String remoteAddress();

    RequestHeader header();

    T data();
}
