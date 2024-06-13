package com.lzh.game.framework.socket.core.session.cache;

import com.lzh.game.framework.socket.core.session.Session;

public interface SessionMemoryCacheManage<k, S extends Session> {

    SessionMemoryCache<k, S> getSessionCache();
}
