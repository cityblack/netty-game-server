package com.lzh.netty.socket.protocol.session.manage;

import com.lzh.netty.socket.protocol.session.DefaultGameSession;
import com.lzh.netty.socket.protocol.session.GameSession;
import com.lzh.netty.socket.protocol.session.Session;
import com.lzh.netty.socket.protocol.session.cache.SessionMemoryCacheManage;
import io.netty.channel.Channel;

public class GameSessionManage extends AbstractSessionManage<GameSession> {

    private SessionMemoryCacheManage<GameSession> cacheManage;

    @Override
    public Session createSession(Channel channel) {
        DefaultGameSession session = new DefaultGameSession(channel);

        session.setCreationTime();
        pushSession(session.getId(), session);
        return session;
    }

    @Override
    public SessionMemoryCacheManage<GameSession> getSessionMemoryCache() {
        return cacheManage;
    }

    public void setCacheManage(SessionMemoryCacheManage<GameSession> cacheManage) {
        this.cacheManage = cacheManage;
    }

    @Override
    public void updateLastAccessTime(GameSession session) {
        if (session instanceof DefaultGameSession) {
            ((DefaultGameSession)session).setLastAccessTime();
        }
    }
}
