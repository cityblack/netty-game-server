package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.core.AsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.framework.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.framework.socket.core.process.FutureResponseProcess;
import com.lzh.game.framework.socket.core.session.Session;
import org.junit.jupiter.api.Test;

public class AppTest {

    @Test
    public void startServer() {
//        GameServerSocketProperties properties = new GameServerSocketProperties();
//        properties.setPort(8081);
//        properties.setOpenGm(true);
//
//        TcpCommonServer server = new TcpCommonServer(properties);
//        ServerDemo demo = new ServerDemo();
//        server.addInvokeBean(demo);
//        server.start();
    }

    @Test
    public void startClient() throws InterruptedException {
        GameSocketProperties properties = new GameSocketProperties();
        properties.setRequestTimeout(5000);
        GameTcpClient client = new GameTcpClient(properties);
//        client.addProcessor(Constant.RESPONSE_SIGN, new FutureResponseProcess());
        client.start();
        Session session = client.conn("localhost", 8081, 5000);
        AsyncResponse<String> future = client.request(session, -10086, "hello world", String.class);
        System.out.println(future.get());
        client.oneWay(session, -10089, "request");
        Thread.sleep(2000);
    }
}
