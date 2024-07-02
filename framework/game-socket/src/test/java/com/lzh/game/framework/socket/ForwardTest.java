package com.lzh.game.framework.socket;

import org.junit.jupiter.api.Test;

public class ForwardTest {

    /**
     * {@link AppTest#startServer()}}
     */
    @Test
    public void startForward() {
//        GameSocketProperties clientProperties = new GameServerSocketProperties();
//        GameTcpClient client = new GameTcpClient(clientProperties);
//        FutureResponseProcess process = new FutureResponseProcess();
//        client.addProcessor(Constant.RESPONSE_SIGN, process);
//        client.start();
//        Session session = client.conn("127.0.0.1", 8081, 2000);
//        client.oneWay(session, -10089, "xx");
//        ForwardSessionSelect strategy = (c, request) -> session;
//        GameServerSocketProperties properties = new GameServerSocketProperties();
//        properties.setPort(8080);
//
//        TcpCommonServer server = new TcpCommonServer(properties);
//        server.addProcessor(Constant.REQUEST_SIGN, new ForwardGatewayProcess(client, strategy, client.getService()));
//        server.addProcessor(Constant.ONEWAY_SIGN, new ForwardGatewayProcess(client, strategy));
//        server.start();
    }

    @Test
    public void startClient() throws InterruptedException {
//        GameSocketProperties properties = new GameSocketProperties();
//        properties.setRequestTimeout(5000);
//        GameTcpClient client = new GameTcpClient(properties);
//        client.addProcessor(Constant.RESPONSE_SIGN, new FutureResponseProcess());
//        client.start();
//        Session session = client.conn("localhost", 8080, 5000);
//        AsyncResponse<String> future = client.request(session, -10086, "hello world", String.class);
//        System.out.println(future.get());
//        client.oneWay(session, -10089, "request");
//        Thread.sleep(2000);
    }
}
