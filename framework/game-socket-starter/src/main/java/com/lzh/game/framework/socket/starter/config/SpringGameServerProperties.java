package com.lzh.game.framework.socket.starter.config;

import com.lzh.game.framework.socket.core.bootstrap.server.ServerSocketProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Use spring properties
 */
@ConfigurationProperties("game.socket.server")
@Configuration
public class SpringGameServerProperties extends ServerSocketProperties {

}
