package com.lzh.netty.client;

import com.lzh.netty.client.bootstrap.GameClientBootstrap;
import com.lzh.netty.client.bootstrap.GameClientHandler;
import com.lzh.netty.client.bootstrap.TcpClient;
import com.lzh.netty.client.dispatcher.ResponseDispatcher;
import com.lzh.netty.client.support.ActionMethodSupport;
import com.lzh.netty.client.support.ActionMethodSupportImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClientProperties.class)
public class Config {

    @Bean
    public ActionMethodSupport methodSupport() {
        return new ActionMethodSupportImpl();
    }

    @Bean
    public ResponseDispatcher responseDispatcher(ActionMethodSupport methodSupport) {
        ResponseDispatcher dispatcher = new ResponseDispatcher(methodSupport);
        return dispatcher;
    }

    @Bean
    public GameClientHandler clientHandler(ResponseDispatcher responseDispatcher) {
        GameClientHandler clientHandler = new GameClientHandler(responseDispatcher);
        return clientHandler;
    }

    @Bean
    public TcpClient tcpClient(ResponseDispatcher responseDispatcher) {
        return new GameClientBootstrap(responseDispatcher);
    }
}
