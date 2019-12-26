package com.lzh.game.start.model.player.log;

import com.lzh.game.start.log.LogFile;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.log.LoggerUtils;
import com.lzh.game.start.model.player.Player;

public class PlayerLog {

    public static void addExp(Player player, int beforeLevel, int afterLevel, long beforeExp, long afterExp, long addExp, LogReason logReason) {
        LoggerUtils.of(LogFile.EXP, logReason)
                .addParam("beforeLevel", beforeLevel)
                .addParam("afterLevel", afterLevel)
                .addParam("beforeExp", beforeExp)
                .addParam("afterExp", afterExp)
                .addParam("addExp", addExp)
                .log(player);
    }
}
