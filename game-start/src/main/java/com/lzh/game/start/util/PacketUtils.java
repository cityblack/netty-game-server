package com.lzh.game.start.util;

import com.lzh.game.socket.dispatcher.ProtoUtil;
import com.lzh.game.socket.exchange.session.Session;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.service.PlayerSessionManage;

public class PacketUtils {

    public static void send(Player player, int cmd) {
        send(player, cmd, null);
    }

    public static void send(Player player, int cmd, Object pack) {

        PlayerSessionManage sessionManage = SpringContext.singleTon().getPlayerSessionManage();
        Session session = sessionManage.findSessionByPlayer(player);
        send(session, cmd, pack);
    }

    public static void send(Session session, int cmd, Object pack) {
        checkCmd(cmd);
        session.write(ProtoUtil.toProBufResponse(cmd, pack));
    }

   private static void checkCmd(int cmd) {
        if (!SpringContext.singleTon().getCmdMappingManage().contain(cmd)) {
            throw new IllegalArgumentException("Not register [" + cmd + "] proto");
        }
   }

    private PacketUtils() {}
}
