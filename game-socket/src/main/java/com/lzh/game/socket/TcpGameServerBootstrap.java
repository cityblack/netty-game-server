package com.lzh.game.socket;

import com.lzh.game.socket.exception.ServerStarException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class TcpGameServerBootstrap implements GameServer {

    private ServerBootstrap bootstrap;
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final int port;
    private final ChannelInitializer<SocketChannel> initializer;
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
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childHandler(initializer);
    }

    private void start(int port) {
        if (Objects.isNull(this.channelFuture)) {
            try {
                channelFuture = this.bootstrap.bind(port);
                this.openTimestamp.set(System.currentTimeMillis());
            } catch (Exception e) {
                throw new ServerStarException("Server start error", e);
            }
            startDaemonAwaitThread(channelFuture);
        }
    }

    private void startDaemonAwaitThread(ChannelFuture future) {
        Thread awaitThread = new Thread("server") {

            @Override
            public void run() {
                try {
                    future.sync().channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    public void close() {
        this.channelFuture = null;
        workerGroup.shutdownGracefully();
    }

    public void start() {
        this.start(this.port);
    }

    @Override
    public void stop() {
        close();
    }

    @Override
    public int getPort() {
        return port;
    }

}
