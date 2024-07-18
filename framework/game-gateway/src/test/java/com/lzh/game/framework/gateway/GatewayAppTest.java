package com.lzh.game.framework.gateway;

import com.lzh.game.framework.gateway.config.GatewayProperties;
import com.lzh.game.framework.gateway.process.ForwardGatewayProcess;
import com.lzh.game.framework.gateway.process.RandomSessionSelect;
import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.GameClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.client.GameTcpClient;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.TcpCommonServer;
import com.lzh.game.framework.socket.core.invoke.ActionRequestHandler;
import com.lzh.game.framework.socket.core.process.impl.DefaultRequestProcess;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;
import com.lzh.game.framework.socket.core.session.GameSessionManage;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionFactory;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.framework.socket.core.session.impl.GameSession;
import com.lzh.game.framework.socket.core.session.monitor.SessionMonitorMange;
import io.netty.handler.logging.LogLevel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zehong.l
 * @since 2024-07-18 14:30
 **/
class GatewayAppTest {

    @Test
    public void server() {
        var properties = new GameServerSocketProperties();
        properties.setPort(8082);
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
    public void gateway() {
        var properties = new GatewayProperties();
        properties.getServerAddress().add("127.0.0.1:8082");
        GatewayClient client = new GatewayClient(properties);
        client.setSessionManage(new SessionMonitorMange<>(new GatewayClientSessionManage(GameSession::of)));
        client.addProcessor(new FutureResponseProcess());
        client.start();

        var serverProperties = properties.getServer();
        serverProperties.setPort(8081);
        serverProperties.setUseDefaultRequest(false);
        serverProperties.setBodyDateToBytes(true);
        var server = new TcpCommonServer<>(properties.getServer());
        server.setSessionManage(sessionManage());
        server.addProcessor(new ForwardGatewayProcess(client, new RandomSessionSelect()));
        server.start();
    }

    @Test
    public void request() throws InterruptedException {
        var properties = new GameClientSocketProperties();
        properties.setConnectTimeout(5000);
        var client = new GameTcpClient<>(properties);
        client.addProcessor(new DefaultRequestProcess(new ActionRequestHandler()));
        client.addProcessor(new FutureResponseProcess());
        client.start();
        Session session = client.conn("localhost", 8081, 5000);
        AsyncResponse<String> future = client.requestCompose(session, (short) -1000, String.class, "hello world");
        System.out.println(future.get());
        client.oneWayCompose(session, (short) -1002, "request");
        Thread.sleep(2000);
    }

    protected SessionManage<Session> sessionManage() {
        SessionFactory<Session> sessionFactory = GameSession::of;
        return new GameSessionManage<>(sessionFactory, new GameSessionMemoryCacheManage<>());
    }
}