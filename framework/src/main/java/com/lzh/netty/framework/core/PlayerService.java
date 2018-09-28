package com.lzh.netty.framework.core;

import com.lzh.netty.framework.core.player.Player;
import com.lzh.netty.socket.protocol.session.Session;

public interface PlayerService {

    void login(Player player, Session session);

}
