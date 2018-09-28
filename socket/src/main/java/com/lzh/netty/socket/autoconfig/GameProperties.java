package com.lzh.netty.socket.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "game")
public class GameProperties {
    private final static int DEFAULT_PORT = 8099;
    private final static int DEFAULT_RESPONSE_TIME = 5; //second
    private final static int SENSE_PROCESS = 5; //count
    private final static int DEFAULT_PROTOCOL_VERSION = 10000; //count
    private final static boolean OPEN_GM = false;

    private int port = DEFAULT_PORT;
    private int responseTime = DEFAULT_RESPONSE_TIME;
    private int sceneProcess = SENSE_PROCESS;
    private Integer protocolVersion = DEFAULT_PROTOCOL_VERSION;
    private boolean openGm = OPEN_GM;
}
