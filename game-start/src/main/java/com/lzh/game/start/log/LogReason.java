package com.lzh.game.start.log;

import lombok.Getter;

public enum LogReason {

    CONSOLE(1001, "控制台"),

    LOGIN(1002, "玩家登陆"),

    USE_ITEM(1004, "使用物品"),

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
