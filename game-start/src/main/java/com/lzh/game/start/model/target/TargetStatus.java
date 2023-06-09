package com.lzh.game.start.model.target;

import lombok.Getter;

/**
 * @author zehong.l
 * @date 2023-06-09 15:16
 **/
public enum TargetStatus {
    NONE(-1, "NOT_COMPLETE"),
    COMPLETED(0, "COMPLETED NOT GAIN"),
    GAINED(1, "GAINED"),

    ;
    @Getter
    private int status;
    private String desc;

    TargetStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
