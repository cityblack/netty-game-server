package com.lzh.game.start.model.player.service;

import com.lzh.game.socket.exchange.session.Session;
import com.lzh.game.start.model.player.Player;

public interface PlayerSessionManage {

    boolean bindSession(Player player, Session session);

    void removePlayerSession(Session session);

    Long findPlayerIdBySession(Session session);

    Session findSessionByPlayer(Player player);
}
