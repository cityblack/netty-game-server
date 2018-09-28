package com.lzh.netty.framework.core.util;

import com.lzh.netty.framework.core.PlayerManage;
import com.lzh.netty.framework.core.player.Player;
import com.lzh.netty.socket.protocol.session.Session;

import java.util.Objects;

public class SessionUtil {

    public static Session getSession(Player player) {
        Objects.requireNonNull(player);
        return playerManage.getSession(player);
    }

    /**
     * {@link com.lzh.netty.framework.core.UtilContext}
     */
    private volatile static PlayerManage playerManage;

    public static void setPlayerManage(PlayerManage manage) {
        playerManage = manage;
    }
}
