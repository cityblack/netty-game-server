package com.lzh.socket.starter;


import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.core.bootstrap.TcpCommonServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TcpServerConfiguration extends GameSocketConfiguration {

    @Bean
    public GameServer gameServer(SessionManage<Session> sessionManage) {
        GameServer server = new TcpCommonServer(getServerSocketProperties(), sessionManage);
        server.asyncStart();
        return server;
    }
}
