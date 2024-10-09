package com.lzh.game.start.log;

import com.lzh.game.framework.logs.param.LogReasonParam;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public enum LogReason implements LogReasonParam {

    NONE(0, "none"),

    CONSOLE(1001, "console"),

    USE_ITEM(2001, "use item"),
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

    @Override
    public int getLogReason() {
        return id;
    }
}
