package com.lzh.game.socket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameServerSocketProperties extends GameSocketProperties {

    private int port = 8099;
    private Integer protocolVersion = 10000;
    private boolean openGm = false;
    private int serverIdleTime = 180000;
}
