package com.lzh.netty.framework.core.player;

import com.lzh.netty.socket.protocol.session.Session;

/**
 * 预留 player对多session
 */
public class PlayerSession {

    private Player player;
    private String sessionId;

    public void changeBindSession(Session session) {
        this.sessionId = session.getId();
    }

    public PlayerSession(Player player, Session session) {
        this.player = player;
        this.sessionId = session.getId();
    }

    public Player getPlayer() {
        return player;
    }

    public String getSession() {
        return sessionId;
    }
}
