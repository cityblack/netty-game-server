package com.lzh.netty.framework.core;

import com.lzh.netty.framework.core.player.Player;
import com.lzh.netty.socket.protocol.session.Session;

public interface PlayerManage {

    boolean bindSession(Player player, Session session);

    Player getPlayer(String sessionId);

    void savePlayer(Player player);

    Session getSession(Player player);
}
