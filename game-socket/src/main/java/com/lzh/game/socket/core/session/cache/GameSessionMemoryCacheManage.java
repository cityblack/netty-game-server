package com.lzh.game.socket.core.session.cache;

import com.lzh.game.socket.core.session.DefaultGameSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameSessionMemoryCacheManage implements SessionMemoryCacheManage<String, DefaultGameSession> {

    private SessionMemoryCache cache = new DefaultSessionMemoryCache();

    @Override
    public SessionMemoryCache getSessionCache() {
        return cache;
    }

}
