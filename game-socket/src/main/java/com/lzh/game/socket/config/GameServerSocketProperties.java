package com.lzh.game.socket.config;

import com.lzh.game.common.scoket.GameSocketProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("game.socket.server")
@EnableConfigurationProperties(GameServerSocketProperties.class)
@Getter
@Setter
public class GameServerSocketProperties extends GameSocketProperties {

    private int port = 8099;
    private Integer protocolVersion = 10000;
    private boolean openGm = false;

}
