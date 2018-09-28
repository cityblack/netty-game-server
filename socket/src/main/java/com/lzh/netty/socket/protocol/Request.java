package com.lzh.netty.socket.protocol;


import com.lzh.netty.socket.protocol.session.Session;

public interface Request {

    Session getSession();

    String getParam(String paramName);

    int getProtocolId();

    String data();

    Integer version();
}
