package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.bean.ServerDemo;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.ClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.tcp.TcpClient;
import com.lzh.game.framework.socket.core.bootstrap.tcp.TcpServer;
import com.lzh.game.framework.socket.core.invoke.ActionRequestHandler;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.core.process.event.ProcessEventListen;
import com.lzh.game.framework.socket.core.process.impl.DefaultRequestProcess;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.proto.RequestData;
import com.lzh.game.framework.socket.proto.ResponseData;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;

@SpringBootTest(classes = AppTest.class)
@Slf4j
public class AppTest {

    @Test
    public void startServer() {
        var properties = new ServerSocketProperties();
        properties.setPort(8081);
        properties.setOpenGm(true);
        properties.getNetty().setLogLevel(LogLevel.INFO);
//        properties.getWebSocket().setEnable(true);
        properties.setAbleAuth(true);
        var server = new TcpServer<>(BootstrapContext.of(properties));
        server.addProcessor(new DefaultRequestProcess(new ActionRequestHandler(server.getContext())));
        server.addProcessEventListen(ProcessEvent.AUTH, new ProcessEventListen() {
            @Override
            public void event(Session session, Object data) {
                session.oneWay(new ResponseData(new RequestData(1L, 2, "xxx", 4.5, 6.7F)));
            }
        });
        ServerDemo demo = new ServerDemo();
        server.addInvokeBean(demo);
        server.start();
    }

    @Test
    public void startClient() throws InterruptedException {
        var properties = new ClientSocketProperties();
        properties.setConnectTimeout(5000);
        var client = new TcpClient<>(BootstrapContext.of(properties));
        // response
        client.addProcessor(new FutureResponseProcess());
        client.start();
        Session session = client.conn("localhost", 8081, 5000);
        log.info("remote: {}", session.getRemoteAddress());
        for (int i = 0; i < 10; i++) {
            var request = new RequestData(-1L, 30, "lzh", 0.1D, 174.3F);
            AsyncResponse<RequestData> future = session.request(request, RequestData.class);
            log.info("i: {} {}", i, future.get());
        }
        session.close();
    }
}
