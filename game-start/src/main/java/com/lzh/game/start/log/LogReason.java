package com.lzh.game.start.log;

import lombok.Getter;

public enum LogReason {

    CONSOLE(1001, "控制台"),

    LOGIN(1002, "玩家登陆"),

    ITEM_ADD_TO_PACK_LOSS(1003, "物品加入背包丢失"),

    USE_ITEM(1004, "使用物品"),

    AUCTION_ON_SALE(1005, "拍卖行上架"),

    COMPOSE(1006, "合成"),

    THROW_DICE(1007, "投骰子"),
    ;

    @Getter
    private int id;

    @Getter
    private String desc;

    LogReason(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
