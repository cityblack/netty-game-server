package com.lzh.game.framework.socket.starter;

import com.lzh.game.framework.socket.GameServerSocketProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Use spring properties
 */
@Configuration
@ConfigurationProperties("game.socket.server")
public class SpringGameServerProperties extends GameServerSocketProperties {

}
