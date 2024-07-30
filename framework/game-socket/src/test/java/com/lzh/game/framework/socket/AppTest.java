package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.bean.ServerDemo;
import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.GameClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.client.GameTcpClient;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.TcpCommonServer;
import com.lzh.game.framework.socket.core.invoke.ActionRequestHandler;
import com.lzh.game.framework.socket.core.invoke.convert.DefaultInvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.support.DefaultActionInvokeSupport;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.impl.DefaultRequestProcess;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;
import com.lzh.game.framework.socket.core.session.Session;
import io.netty.handler.logging.LogLevel;
import org.junit.jupiter.api.Test;

public class AppTest {

    @Test
    public void startServer() {
        var properties = new GameServerSocketProperties();
        properties.setPort(8081);
        properties.setOpenGm(true);
        properties.setBossWordCore(1);
        properties.setUseEpoll(true);
        properties.getNetty().setLogLevel(LogLevel.INFO);

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
        // response
        client.addProcessor(new FutureResponseProcess());
        client.start();
        Session session = client.conn("localhost", 8081, 5000);
        AsyncResponse<String> future = client.requestCompose(session, (short) -1000, String.class, "hello world");
        System.out.println(future.get());
        client.oneWayCompose(session, (short) -1002, "request");
        Thread.sleep(2000);
    }
}
