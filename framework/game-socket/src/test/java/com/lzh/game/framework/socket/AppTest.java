package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.bean.ServerDemo;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.GameClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.client.GameTcpClient;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.TcpServer;
import com.lzh.game.framework.socket.core.invoke.ActionRequestHandler;
import com.lzh.game.framework.socket.core.invoke.convert.DefaultInvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.process.impl.DefaultRequestProcess;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.proto.RequestData;
import io.netty.handler.logging.LogLevel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AppTest.class)
public class AppTest {

    @Test
    public void startServer() {
        var properties = new GameServerSocketProperties();
        properties.setPort(8081);
        properties.setOpenGm(true);
        properties.getNetty().setLogLevel(LogLevel.INFO);

        var server = new TcpServer<>(BootstrapContext.of(properties));
        server.addProcessor(new DefaultRequestProcess(new ActionRequestHandler(server.getContext(), new DefaultInvokeMethodArgumentValues())));
        ServerDemo demo = new ServerDemo();
        server.addInvokeBean(demo);
        server.start();
    }

    @Test
    public void startClient() throws InterruptedException {
        var properties = new GameClientSocketProperties();
        properties.setConnectTimeout(5000);
        var client = new GameTcpClient<>(BootstrapContext.of(properties));
        // response
        client.addProcessor(new FutureResponseProcess());
        client.start();
        Session session = client.conn("localhost", 8081, 5000);
        var request = new RequestData(1L, 30, "lzh", 0.1D, 174.3F);
        AsyncResponse<RequestData> future = client.request(session, request, RequestData.class);
        System.out.println(future.get());
    }
}
