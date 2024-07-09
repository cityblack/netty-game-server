package com.lzh.game.framework.socket.core.session.monitor;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class SeasonMonitorConfig {

    private long checkTimeInterval = TimeUnit.SECONDS.toMillis(10);
    // Zero is not limit retry connect times
    private int reConnMaxCount;

}
