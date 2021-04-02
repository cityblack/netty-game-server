package com.lzh.game.start.log;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

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

    public static void check() {
        Set<Integer> ids = new HashSet<>(LogReason.values().length);
        Stream.of(LogReason.values()).forEach(e -> {
            int id = e.id;
            if (ids.contains(id)) {
                throw new IllegalArgumentException("The logReason id [" + id + "] not unique.");
            }
            ids.add(id);
        });
    }
}
