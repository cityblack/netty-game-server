package com.lzh.game.socket;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.annotation.RequestMapping;
import com.lzh.game.socket.annotation.ResponseMapping;
import com.lzh.game.socket.core.AsyncResponse;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.socket.core.process.FutureResponseProcess;
import com.lzh.game.socket.core.session.Session;
import org.junit.jupiter.api.Test;

public class AppTest {

    public class ServerDemo {

        @RequestMapping(-10086)
        @ResponseMapping(-10087)
        public String hello(String hello) {
            System.out.println(hello);
            return "server say:" + hello;
        }

        @RequestMapping(-10089)
        public void noeWay(String one) {
            System.out.println("one way:" + one);
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

    @Test
    public void startClient() throws InterruptedException {
        GameSocketProperties properties = new GameSocketProperties();
        properties.setRequestTimeout(5000);
        GameTcpClient client = new GameTcpClient(properties);
        client.addProcess(Constant.RESPONSE_COMMAND_KEY, new FutureResponseProcess());
        client.start();
        Session session = client.conn("localhost", 8081, 5000);
        AsyncResponse<String> future = client.request(session, -10086, "hello world", String.class);
        System.out.println(future.get());
        client.oneWay(session, -10089, "request");
        Thread.sleep(2000);
    }
}
