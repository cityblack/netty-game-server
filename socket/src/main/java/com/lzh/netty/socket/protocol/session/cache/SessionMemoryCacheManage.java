package com.lzh.netty.socket.protocol.session.cache;


import com.lzh.netty.socket.protocol.session.Session;

public interface SessionMemoryCacheManage<S extends Session> {

    void put(String key,S value);

    S remove(String key);

    S get(String key);
}
