package com.lzh.game.framework.gateway;

import com.lzh.game.framework.gateway.config.GatewayProperties;
import com.lzh.game.framework.gateway.process.ForwardGatewayProcess;
import com.lzh.game.framework.gateway.process.RandomSessionSelect;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.GameClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.tcp.TcpClient;
import com.lzh.game.framework.socket.core.bootstrap.tcp.TcpServer;
import com.lzh.game.framework.socket.core.invoke.ActionRequestHandler;
import com.lzh.game.framework.socket.core.invoke.convert.DefaultInvokeMethodArgumentValues;
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
        var properties = new GameServerSocketProperties();
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
        properties.getClient().setBodyDateToBytes(true);
        GatewayClient client = new GatewayClient(properties);
        client.addProcessor(new FutureResponseProcess());
        client.start();

        var serverProperties = properties.getServer();
        serverProperties.setPort(8081);
        serverProperties.setBodyDateToBytes(true);
        var server = new TcpServer<>(BootstrapContext.of(properties.getServer()));
        server.addProcessor(new ForwardGatewayProcess(new RandomSessionSelect(client)));
        server.start();
    }

    @Test
    public void request() throws InterruptedException {
        var properties = new GameClientSocketProperties();
        properties.setConnectTimeout(5000);
        var client = new TcpClient<>(BootstrapContext.of(properties));
        client.addProcessor(new FutureResponseProcess());
        client.start();
        Session session = client.conn("localhost", 8081, 10000);
        var future = session.request(new RequestData(), HelloWordResponse.class);

        System.out.println(future.get());
    }

}