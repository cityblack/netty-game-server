package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.bean.ServerDemo;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.ClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.tcp.TcpClient;
import com.lzh.game.framework.socket.core.bootstrap.tcp.TcpServer;
import com.lzh.game.framework.socket.core.invoke.ActionRequestHandler;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.ProcessorExecutorService;
import com.lzh.game.framework.socket.core.process.context.ProcessorContext;
import com.lzh.game.framework.socket.core.process.impl.DefaultRequestProcess;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionEvent;
import com.lzh.game.framework.socket.proto.RequestData;
import com.lzh.game.framework.socket.utils.SocketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

@SpringBootTest(classes = AppTest.class)
@Slf4j
public class AppTest {

    @Test
    public void startServer() {
        var properties = new ServerSocketProperties();
        properties.setPort(8081);
        properties.setOpenGm(true);
        properties.getNetty().setLogLevel(LogLevel.INFO);
        properties.getWebSocket().setAble(true);
        properties.setAbleAuth(false);
        var server = new TcpServer<>(BootstrapContext.of(properties));
//        server.getContext().getSessionManage().addListener(SessionEvent.CONNECT, ((session, o) -> {
//            var request = SocketUtils.createOneWayRequest(-10086, new RequestData());
//            session.write(request);
//        }));
//        server.addProcessor(new DefaultRequestProcess(new ActionRequestHandler(server.getContext())));
        server.addProcessor(new Processor() {
            @Override
            public void process(ProcessorContext context, Session session, Object data) {
                if (data instanceof Request request) {
                    var msg = SocketUtils.createOneWayRequest(-10086, request.getData());
                    session.write(msg);
                }

            }

            @Override
            public boolean match(Session session, Object msg) {
                return true;
            }

            @Override
            public ProcessorExecutorService service() {
                return null;
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
