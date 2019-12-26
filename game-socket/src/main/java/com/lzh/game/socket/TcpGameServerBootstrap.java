package com.lzh.game.socket;

import com.lzh.game.common.server.BeforeServerCloseEvent;
import com.lzh.game.common.server.BeforeServerStartEvent;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class TcpGameServerBootstrap implements GameServer, DisposableBean {

    private ServerBootstrap bootstrap;
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final int port;
    private final ChannelInitializer<SocketChannel> initializer;
    private AtomicBoolean start = new AtomicBoolean(false);
    private volatile ChannelFuture channelFuture;
    private AtomicLong openTimestamp = new AtomicLong();

    public TcpGameServerBootstrap(int port, ChannelInitializer<SocketChannel> initializer) {
        this.port = port;
        this.initializer = initializer;
        init();
    }

    protected void init() {
        this.bootstrap = new ServerBootstrap();
        bootstrap.group(workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(initializer);
    }

    private void start(int port) {
        if (!start.get()) {
            ChannelFuture f = null;
            sendBeforeStartServerEvent();
            try {
                f = this.bootstrap.bind(port).sync();
                this.start.set(true);
                channelFuture = f;
                this.openTimestamp.set(System.currentTimeMillis());
                log.info("Netty started on port(s): {}", port);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        sendBeforeCloseServerEvent();
        workerGroup.shutdownGracefully();
    }

    public void start() {
        this.start(this.port);
    }

    @Override
    public void stop() {
        close();
        System.exit(-1);
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isStart() {
        return this.start.get();
    }

    @Override
    public void destroy() throws Exception {
        close();
    }

    private void sendBeforeStartServerEvent() {
        BeforeServerStartEvent message = new BeforeServerStartEvent();
        message.setPort(this.port);
        message.setTimestamp(System.currentTimeMillis());
        EventBus.getDefault().post(message);
    }

    private void sendBeforeCloseServerEvent() {
        BeforeServerCloseEvent message = new BeforeServerCloseEvent();
        message.setPort(this.port);
        message.setTimestamp(System.currentTimeMillis());
        EventBus.getDefault().post(message);
    }
}
