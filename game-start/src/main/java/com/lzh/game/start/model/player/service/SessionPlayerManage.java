package com.lzh.game.start.model.player.service;

import com.lzh.game.socket.core.session.Session;

public interface SessionPlayerManage {

    Session getSessionByPlayer(long playerId);
}
