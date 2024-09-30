package com.lzh.game.framework.socket.core.bootstrap.server;

import com.lzh.game.framework.socket.core.GameSocketProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class GameServerSocketProperties extends GameSocketProperties {

    private int port = 8080;
    private Integer protocolVersion = 10000;
    private boolean openGm = false;
    private long serverIdleTime = TimeUnit.MINUTES.toMillis(3);

}
