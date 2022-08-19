package com.lzh.game.start.util;

import com.lzh.game.common.ApplicationUtils;
import com.lzh.game.common.util.Constant;
import com.lzh.game.framework.cmd.CmdMappingManage;
import com.lzh.game.socket.SocketUtils;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.service.SessionPlayerManage;

import java.util.Objects;

public class PacketUtils {

    public static void send(Player player, int cmd) {
        send(player, cmd, null);
    }

    public static void send(Player player, int cmd, Object pack) {
        SessionPlayerManage sessionManage = ApplicationUtils.getBean(SessionPlayerManage.class);
        Session session = sessionManage.getSessionByPlayer(player.getKey());
        send(session, cmd, pack);
    }

    public static void send(Session session, int cmd, Object pack) {
        checkCmd(cmd);
        session.write(SocketUtils.createOneWayRequest(cmd, pack));
    }

    private static CmdMappingManage cmdMappingManage;

    private static void checkCmd(int cmd) {
        if (Objects.isNull(cmdMappingManage)) {
            synchronized (PacketUtils.class) {
                if (Objects.isNull(cmdMappingManage)) {
                    // Spring find bean maybe slow
                    cmdMappingManage = ApplicationUtils.getBean(CmdMappingManage.class);
                }
            }
        }
        if (!cmdMappingManage.contain(cmd)) {
            throw new IllegalArgumentException("Not register [" + cmd + "] proto");
        }
    }

    private PacketUtils() {
    }
}
