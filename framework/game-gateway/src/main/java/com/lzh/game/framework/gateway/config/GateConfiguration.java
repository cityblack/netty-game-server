package com.lzh.game.framework.gateway.config;

import com.lzh.game.framework.gateway.GatewayClient;
import com.lzh.game.framework.gateway.process.ForwardGatewayProcess;
import com.lzh.game.framework.gateway.process.RandomSessionSelect;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.TcpCommonServer;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;
import com.lzh.game.framework.socket.core.session.GameSessionManage;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionFactory;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.framework.socket.core.session.impl.GameSession;
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
    public TcpCommonServer<GameServerSocketProperties> gameServer(GatewayClient client) {
        properties.getServer().setUseDefaultRequest(false);

        var server = new TcpCommonServer<>(properties.getServer(), BootstrapContext.of());
        server.addProcessor(new ForwardGatewayProcess(client, new RandomSessionSelect(client)));
        server.asyncStart();
        return server;
    }

    @Bean
    public GatewayClient client() {
        GatewayClient client = new GatewayClient(properties, BootstrapContext.of());
        client.addProcessor(new FutureResponseProcess());
        client.start();
        return client;
    }

}
