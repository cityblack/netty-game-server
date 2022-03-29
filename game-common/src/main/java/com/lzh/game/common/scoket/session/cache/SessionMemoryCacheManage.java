package com.lzh.game.common.scoket.session.cache;


import com.lzh.game.common.scoket.session.Session;

public interface SessionMemoryCacheManage<k, S extends Session> {

    SessionMemoryCache<k, S> getSessionCache();
}
