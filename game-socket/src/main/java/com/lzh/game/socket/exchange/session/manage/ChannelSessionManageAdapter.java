package com.lzh.game.socket.exchange.session.manage;

import com.lzh.game.socket.exchange.session.ChannelSessionManage;
import com.lzh.game.socket.exchange.session.DefaultGameSession;
import com.lzh.game.socket.exchange.session.Session;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelSessionManageAdapter implements ChannelSessionManage {

    private GameSessionManage gameSessionManage;
    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    @Override
    public Session createSession(Channel channel) {
        DefaultGameSession session = new DefaultGameSession(channel);
        session.setCreationTime();
        gameSessionManage.pushSession(session.getId(), session);
        channelMap.put(session.getId(), channel);
        return session;
    }

    @Override
    public Session getSession(Channel channel) {

        return gameSessionManage.getSession(channel.id().asLongText());
    }

    @Override
    public Session closeSession(Channel channel) {
        return gameSessionManage.closeSession(channel.id().asLongText());
    }

    @Override
    public Channel getChannelBySession(Session session) {
        return channelMap.get(session.getId());
    }

    public void setGameSessionManage(GameSessionManage gameSessionManage) {
        this.gameSessionManage = gameSessionManage;
    }
}
