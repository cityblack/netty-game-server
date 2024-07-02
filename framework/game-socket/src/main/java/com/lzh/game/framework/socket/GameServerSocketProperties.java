package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.core.protocol.serial.impl.fury.FuryProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GameServerSocketProperties extends GameSocketProperties {

    private int port = 8099;
    private Integer protocolVersion = 10000;
    private boolean openGm = false;
    private int serverIdleTime = 180000;
    private int bossWordCore = 2;
    private boolean useEpoll = true;

    private Map<String, Object> channelOptions = new HashMap<>();

    private Map<String, Object> childOptions = new HashMap<>();

}
