package com.lzh.game.framework.gateway.config;

import com.lzh.game.framework.socket.core.bootstrap.client.GameClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("game.gateway")
@Getter
@Setter
public class GatewayProperties {

    @Getter
    @Setter
    private List<String> serverAddress = new ArrayList<>();

    private GameServerSocketProperties server;

    private GameClientSocketProperties client;
}
