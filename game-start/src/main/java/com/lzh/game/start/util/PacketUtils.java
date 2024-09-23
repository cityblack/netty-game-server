package com.lzh.game.start.util;

import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.utils.SocketUtils;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.service.SessionPlayerManage;

public class PacketUtils {


    public static void send(Player player, Object pack) {
        SessionPlayerManage sessionManage = ApplicationUtils.getBean(SessionPlayerManage.class);
        Session session = sessionManage.getSessionByPlayer(player.getKey());
        send(session, pack);
    }

    public static void send(Session session, Object pack) {
        var defined = ApplicationUtils.getBean(MessageManager.class)
                .findDefine(pack.getClass());

        session.write(SocketUtils.createOneWayRequest(defined.getMsgId(), pack));
    }

    private PacketUtils() {
    }
}
