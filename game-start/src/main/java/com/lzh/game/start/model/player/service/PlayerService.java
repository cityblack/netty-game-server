package com.lzh.game.start.model.player.service;

import com.lzh.game.socket.core.session.Session;
import com.lzh.game.start.model.player.Player;

public interface PlayerService {

    Player getPlayer(Session session);

    void save(Player player);
}
