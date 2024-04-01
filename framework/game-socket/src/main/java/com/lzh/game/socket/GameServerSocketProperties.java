package com.lzh.game.socket;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
public class GameServerSocketProperties extends GameSocketProperties {

    private int port = 8099;
    private Integer protocolVersion = 10000;
    private boolean openGm = false;

}
