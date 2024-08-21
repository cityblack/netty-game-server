package com.lzh.game.framework.gateway.config;

import com.lzh.game.framework.gateway.GatewayClient;
import com.lzh.game.framework.gateway.process.ForwardGatewayProcess;
import com.lzh.game.framework.gateway.process.RandomSessionSelect;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.TcpServer;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class GateConfiguration {

    @Resource
    private GatewayProperties properties;

    @Bean
    public TcpServer<GameServerSocketProperties> gameServer(GatewayClient client) {
        var server = new TcpServer<>(BootstrapContext.of(properties.getServer()));
        server.addProcessor(new ForwardGatewayProcess(client, new RandomSessionSelect(client)));
        server.asyncStart();
        return server;
    }

    @Bean
    public GatewayClient client() {
        GatewayClient client = new GatewayClient(properties);
        client.addProcessor(new FutureResponseProcess());
        client.start();
        return client;
    }

}
