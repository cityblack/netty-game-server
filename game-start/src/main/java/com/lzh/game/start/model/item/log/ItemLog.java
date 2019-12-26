package com.lzh.game.start.model.item.log;

import com.lzh.game.start.log.LogFile;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.log.LoggerUtils;
import com.lzh.game.start.model.item.bag.model.RemoveItem;
import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.player.Player;

public class ItemLog {
    /**
     * 物品丢失日志
     *
     * @param player
     * @param item
     */
    public static void lossItem(Player player, AbstractItem item, LogReason logReason) {
        LoggerUtils.of(LogFile.ITEM, logReason)
                .addParam("itemModel", item.getResourceId())
                .addParam("itemNum", item.getNum())
                .addParam("type", "loss")
                .log(player);
    }

    /**
     * 添加物品日志
     *
     * @param player
     * @param item
     */
    public static void addItem(Player player, AbstractItem item, LogReason logReason) {
        LoggerUtils.of(LogFile.ITEM, logReason)
                .addParam("itemModel", item.getResourceId())
                .addParam("itemNum", item.getNum())
                .addParam("type","add")
                .log(player);
    }

    /**
     * 减少物品日志
     *
     * @param player
     * @param item
     */
    public static void reduceItem(Player player, RemoveItem item, LogReason logReason) {
        LoggerUtils.of(LogFile.ITEM, logReason)
                .addParam("itemModel", item.getItem().getResourceId())
                .addParam("id", item.getItem().getObjectId())
                .addParam("num", item.getNum())
                .addParam("index", item.getSourceIndex())
                .addParam("remain", item.getItem().getNum())
                .addParam("type", "reduce")
                .log(player);
    }
}
