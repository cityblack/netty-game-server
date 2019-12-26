package com.lzh.game.start.model.player.service.impl;

import com.lzh.game.socket.exchange.session.Session;
import com.lzh.game.socket.exchange.session.SessionManage;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.service.PlayerSessionManage;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PlayerSessionManageImpl implements PlayerSessionManage {

    private final String SESSION_PLAYER = "session_player";

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SessionManage<Session> sessionManage;

    @Override
    public boolean bindSession(Player player, Session session) {
        long playerId = player.getObjectId();
        session.setAttribute(SESSION_PLAYER, playerId);
        redissonClient.getMap(SESSION_PLAYER).put(playerId, session.getId());
        return true;
    }

    @Override
    public void removePlayerSession(Session session) {
        Long playerId = session.getAttribute(SESSION_PLAYER);

        if (Objects.nonNull(playerId)) {
            session.getAttributes().remove(SESSION_PLAYER);
            redissonClient.getMap(SESSION_PLAYER).remove(playerId);
        }
    }

    @Override
    public Long findPlayerIdBySession(Session session) {

        return session.getAttribute(SESSION_PLAYER);
    }

    @Override
    public Session findSessionByPlayer(Player player) {
        Object sessionId = redissonClient.getMap(SESSION_PLAYER).get(player.getObjectId());
        if (Objects.isNull(sessionId)) {
            throw new IllegalArgumentException("Session is not exist");
        }
        return sessionManage.getSession((String) sessionId);
    }
}
