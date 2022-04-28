package com.lzh.game.socket;

import io.netty.handler.logging.LogLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
public class GameSocketProperties {

    private LogLevel nettyLogLevel = LogLevel.ERROR;
}
