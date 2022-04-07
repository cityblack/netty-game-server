package com.lzh.game.common.scoket.session.cache;

import com.lzh.game.common.scoket.session.Session;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameSessionMemoryCacheManage<S extends Session> implements SessionMemoryCacheManage<String, S> {

    private SessionMemoryCache<String, S> cache = new DefaultSessionMemoryCache<>();

    @Override
    public SessionMemoryCache<String, S> getSessionCache() {
        return cache;
    }

}
