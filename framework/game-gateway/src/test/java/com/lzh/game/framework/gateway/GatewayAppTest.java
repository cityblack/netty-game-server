package com.lzh.game.framework.gateway;

import com.lzh.game.framework.gateway.config.GatewayProperties;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.client.ClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.tcp.TcpClient;
import com.lzh.game.framework.socket.core.bootstrap.tcp.TcpServer;
import com.lzh.game.framework.socket.core.invoke.ActionRequestHandler;
import com.lzh.game.framework.socket.core.process.impl.DefaultRequestProcess;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;
import com.lzh.game.framework.socket.core.session.Session;
import io.netty.handler.logging.LogLevel;
import org.junit.jupiter.api.Test;

/**
 * @author zehong.l
 * @since 2024-07-18 14:30
 **/
class GatewayAppTest {

    @Test
    public void server() {
        var properties = new ServerSocketProperties();
        properties.setPort(8082);
        properties.setOpenGm(true);
        properties.getNetty().setLogLevel(LogLevel.DEBUG);
        var server = new TcpServer<>(BootstrapContext.of(properties));
        server.addProcessor(new DefaultRequestProcess(new ActionRequestHandler(server.getContext())));
        ServerDemo demo = new ServerDemo();
        server.addInvokeBean(demo);
        server.start();
    }

    @Test
    public void gateway() throws InterruptedException {
        var properties = new GatewayProperties();
        properties.getServerAddress().add("127.0.0.1:8082");
        properties.getServer().setPort(8081);
        var gateway = new GateWay(properties);
        gateway.start();
    }

    @Test
    public void request() throws InterruptedException {
        var properties = new ClientSocketProperties();
        properties.setConnectTimeout(5000);
        var client = new TcpClient<>(BootstrapContext.of(properties));
        client.addProcessor(new FutureResponseProcess());
        client.start();
        Session session = client.conn("localhost", 8081, 10000);
        var future = session.request(new RequestData(), HelloWordResponse.class);

        System.out.println(future.get());
    }

}