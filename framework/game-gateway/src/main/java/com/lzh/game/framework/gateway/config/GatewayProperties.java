package com.lzh.game.framework.gateway.config;

import com.lzh.game.framework.socket.core.bootstrap.client.ClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerSocketProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties("game.gateway")
public class GatewayProperties {

    private List<String> serverAddress = new ArrayList<>();

    private ServerSocketProperties server = new ServerSocketProperties();

    private ClientSocketProperties client = new ClientSocketProperties();
}
