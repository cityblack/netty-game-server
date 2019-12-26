package com.lzh.game.socket.exchange.session.cache;


import com.lzh.game.socket.exchange.session.Session;

public interface SessionMemoryCacheManage<S extends Session> {

    void put(String key, S value);

    S remove(String key);

    S get(String key);
}
