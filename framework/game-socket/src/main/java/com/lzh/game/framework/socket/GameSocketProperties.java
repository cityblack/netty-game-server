package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.core.protocol.serial.impl.fury.FuryProperties;
import io.netty.handler.logging.LogLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSocketProperties {

    private LogLevel nettyLogLevel = LogLevel.ERROR;

    private int requestTimeout = 2000;

    private FuryProperties fury = new FuryProperties();
}
