package com.lzh.netty.socket.protocol;


import com.lzh.netty.socket.protocol.session.Session;

public interface ProtocolExchange {

    void write(Session session, Object o);
}
