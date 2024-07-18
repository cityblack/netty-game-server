package com.lzh.game.framework.gateway.config;

import com.lzh.game.framework.socket.core.bootstrap.client.GameClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties("game.gateway")
public class GatewayProperties {

    private List<String> serverAddress = new ArrayList<>();

    private GameServerSocketProperties server = new GameServerSocketProperties();

    private GameClientSocketProperties client = new GameClientSocketProperties();
}
