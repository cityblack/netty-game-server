package com.lzh.game.framework.socket.core.bootstrap;

import io.netty.handler.logging.LogLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-07-03 10:50
 **/
@Getter
@Setter
public class NettyProperties {

    private LogLevel logLevel = LogLevel.DEBUG;

    private Map<String, Object> channelOptions = new HashMap<>();

    private Map<String, Object> childOptions = new HashMap<>();

    private boolean useEpoll = true;

    private int bossWordCore = 1;

    // use default core
    private int workCore = 0;
}
