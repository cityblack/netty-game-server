package com.lzh.game.start.model.player.service;

import com.lzh.game.common.scoket.session.Session;

public interface SessionPlayerManage {

    Session getSessionByPlayer(long playerId);
}
