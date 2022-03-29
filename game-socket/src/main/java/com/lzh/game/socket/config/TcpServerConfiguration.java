package com.lzh.game.socket.config;

import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.common.scoket.session.SessionManage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TcpServerConfiguration extends GameSocketConfiguration {

    @Bean
    public GameServer gameServer(SessionManage sessionManage) {
        GameServer server = new TcpCommonServer(getServerSocketProperties(), sessionManage);
        return server;
    }
}
