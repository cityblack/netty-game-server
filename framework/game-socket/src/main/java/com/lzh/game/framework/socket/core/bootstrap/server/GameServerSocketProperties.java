package com.lzh.game.framework.socket.core.bootstrap.server;

import com.lzh.game.framework.socket.core.GameSocketProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameServerSocketProperties extends GameSocketProperties {

    private int port = 8080;
    private Integer protocolVersion = 10000;
    private boolean openGm = false;
    private int serverIdleTime = 180000;

}
