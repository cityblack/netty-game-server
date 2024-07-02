package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.core.bootstrap.client.GameTcpClient;
import com.lzh.game.framework.socket.core.bootstrap.server.TcpCommonServer;
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
        var properties = new GameSocketProperties();
        properties.setRequestTimeout(5000);
        GameTcpClient client = new GameTcpClient(properties);
//        client.addProcessor(Constant.RESPONSE_SIGN, new FutureResponseProcess());
        client.start();
//        Session session = client.conn("localhost", 8081, 5000);
//        AsyncResponse<String> future = client.request(session, -10086, "hello world", String.class);
//        System.out.println(future.get());
//        client.oneWay(session, (short) -10089, "request");
//        Thread.sleep(2000);
    }
}
