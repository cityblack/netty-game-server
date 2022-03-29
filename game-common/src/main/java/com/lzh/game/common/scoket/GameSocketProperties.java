package com.lzh.game.common.scoket;

import io.netty.handler.logging.LogLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "game.socket")
public class GameSocketProperties {

    private LogLevel nettyLogLevel = LogLevel.ERROR;
}
