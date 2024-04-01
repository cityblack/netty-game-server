package com.lzh.game.socket.core.session.cache;

import com.lzh.game.socket.core.session.Session;

public interface SessionMemoryCacheManage<k, S extends Session> {

    SessionMemoryCache<k, S> getSessionCache();
}
