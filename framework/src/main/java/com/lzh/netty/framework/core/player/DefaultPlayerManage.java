package com.lzh.netty.framework.core.player;

import com.lzh.netty.framework.core.PlayerManage;
import com.lzh.netty.socket.protocol.session.GameSession;
import com.lzh.netty.socket.protocol.session.Session;
import com.lzh.netty.socket.protocol.session.SessionManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DefaultPlayerManage implements PlayerManage {
    @Autowired
    private SessionManage<GameSession> sessionManage;
    private Map<String, PlayerSession> playerSession = new ConcurrentHashMap<>();

    @Override
    public boolean bindSession(Player player, Session session) {

        if (Objects.nonNull(player) && Objects.nonNull(session)) {
            playerSession.put(player.getId(), new PlayerSession(player,session));
            return true;
        }
        return false;
    }

    @Override
    public Player getPlayer(String sessionId) {
        return null;
    }

    @Override
    public void savePlayer(Player player) {

    }

    @Override
    public Session getSession(Player player) {

        if (Objects.nonNull(player) && playerSession.containsKey(player.getId())) {

            return sessionManage.getSession(playerSession.get(player.getId()).getSession());
        }
        return null;
    }
}
