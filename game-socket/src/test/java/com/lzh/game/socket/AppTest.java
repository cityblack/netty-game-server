package com.lzh.game.socket;

import com.lzh.game.socket.annotation.RequestMapping;
import com.lzh.game.socket.annotation.ResponseMapping;
import com.lzh.game.socket.core.bootstrap.TcpCommonServer;
import org.junit.jupiter.api.Test;

public class AppTest {

    public class ServerDemo {

        @RequestMapping(-10086)
        @ResponseMapping(-10087)
        public String hello(String hello) {
            System.out.println(hello);
            return "server say:" + hello;
        }
    }

    @Test
    public void startServer() {
        GameServerSocketProperties properties = new GameServerSocketProperties();
        properties.setPort(8081);
        properties.setOpenGm(true);

        TcpCommonServer server = new TcpCommonServer(properties);
        ServerDemo demo = new ServerDemo();
        server.addInvokeBean(demo);
        server.start();
    }
}
