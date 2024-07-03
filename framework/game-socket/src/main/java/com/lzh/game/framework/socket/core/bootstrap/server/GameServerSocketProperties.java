package com.lzh.game.framework.socket.core.bootstrap.server;

import com.lzh.game.framework.socket.GameSocketProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GameServerSocketProperties extends GameSocketProperties {

    private int port = 8080;
    private Integer protocolVersion = 10000;
    private boolean openGm = false;
    private int serverIdleTime = 180000;
    private int bossWordCore = 2;
    private boolean useEpoll = true;


}
