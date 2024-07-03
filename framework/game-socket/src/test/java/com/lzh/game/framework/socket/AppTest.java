package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.bean.ServerDemo;
import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.GameClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.client.GameTcpClient;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.TcpCommonServer;
import com.lzh.game.framework.socket.core.session.Session;
import org.junit.jupiter.api.Test;

public class AppTest {

    @Test
    public void startServer() {
        var properties = new GameServerSocketProperties();
        properties.setPort(8081);
        properties.setOpenGm(true);
        properties.setBossWordCore(1);
        properties.setUseEpoll(true);

        var server = new TcpCommonServer<>(properties);
        ServerDemo demo = new ServerDemo();
        server.addInvokeBean(demo);
        server.start();
    }

    @Test
    public void startClient() throws InterruptedException {
        var properties = new GameClientSocketProperties();
        properties.setConnectTimeout(5000);
        var client = new GameTcpClient<>(properties);
        client.start();
        Session session = client.conn("localhost", 8081, 5000);
        AsyncResponse<String> future = client.requestCompose(session, (short) -10086, "hello world");
        System.out.println(future.get());
        client.oneWayCompose(session, (short) -10089, "request");
        Thread.sleep(2000);
    }
}
