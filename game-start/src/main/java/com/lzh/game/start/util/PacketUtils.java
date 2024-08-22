package com.lzh.game.start.util;

import com.lzh.game.framework.socket.utils.SocketUtils;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.service.SessionPlayerManage;

public class PacketUtils {

    public static void send(Player player, short cmd) {
        send(player, cmd, null);
    }

    public static void send(Player player, short cmd, Object pack) {
        SessionPlayerManage sessionManage = ApplicationUtils.getBean(SessionPlayerManage.class);
        Session session = sessionManage.getSessionByPlayer(player.getKey());
        send(session, cmd, pack);
    }

    public static void send(Session session, short cmd, Object pack) {
        session.write(SocketUtils.createOneWayRequest(cmd, pack));
    }

    private PacketUtils() {
    }
}
