package com.lzh.game.socket.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "game.socket")
public class GameSocketProperties {

    private int port = 8099;
    private Integer protocolVersion = 10000;
    private boolean openGm = false;

}
