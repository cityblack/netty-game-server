package com.lzh.socket.starter;

import com.lzh.game.socket.GameServerSocketProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Use spring properties
 */
@Configuration
@ConfigurationProperties("game.socket.server")
public class SpringGameServerProperties extends GameServerSocketProperties {

}
