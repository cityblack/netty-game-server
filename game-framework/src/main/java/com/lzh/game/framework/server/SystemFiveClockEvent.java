package com.lzh.game.framework.server;

import lombok.Data;

/**
 * Day five clock event model
 */
@Data
public class SystemFiveClockEvent {

    private long time;

    public long getTime() {
        return time;
    }

    public static SystemFiveClockEvent of(long time) {
        SystemFiveClockEvent event = new SystemFiveClockEvent();
        event.time = time;
        return event;
    }
}
