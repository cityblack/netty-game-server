package com.lzh.game.socket.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GameServerSocketProperties.class)
public class GameServerSocketProperties extends GameSocketProperties {


}
