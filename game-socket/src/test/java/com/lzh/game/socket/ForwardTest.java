package com.lzh.game.socket;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.core.AsyncResponse;
import com.lzh.game.socket.core.ForwardStrategy;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.socket.core.process.ForwardGateway;
import com.lzh.game.socket.core.process.FutureResponseProcess;
import com.lzh.game.socket.core.session.Session;
import org.junit.jupiter.api.Test;

public class ForwardTest {

    /**
     * {@link AppTest#startServer()}}
     */
    @Test
    public void startForward() {
        GameSocketProperties clientProperties = new GameServerSocketProperties();
        GameTcpClient client = new GameTcpClient(clientProperties);
        client.addProcess(Constant.RESPONSE_COMMAND_KEY, new FutureResponseProcess());
        client.start();
        Session session = client.conn("127.0.0.1", 8081, 2000);
        client.oneWay(session, -10089, "xx");

        ForwardStrategy strategy = (c, request) -> session;

        GameServerSocketProperties properties = new GameServerSocketProperties();
        properties.setPort(8080);

        TcpCommonServer server = new TcpCommonServer(properties);
        server.addProcess(Constant.REQUEST_COMMAND_KEY, new ForwardGateway(client, strategy));
        server.start();
    }

    @Test
    public void startClient() throws InterruptedException {
        GameSocketProperties properties = new GameSocketProperties();
        properties.setRequestTimeout(5000);
        GameTcpClient client = new GameTcpClient(properties);
        client.addProcess(Constant.RESPONSE_COMMAND_KEY, new FutureResponseProcess());
        client.start();
        Session session = client.conn("localhost", 8080, 5000);
        AsyncResponse<String> future = client.request(session, -10086, "hello world", String.class);
        System.out.println(future.get());
        client.oneWay(session, -10089, "request");
        Thread.sleep(2000);
    }
}
