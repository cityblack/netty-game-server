package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.bootstrap.server.GameServer;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerSocketProperties;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.impl.AbstractSession;
import com.lzh.game.framework.socket.exception.ServerStarException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@Setter
@Slf4j
public class NetServer implements GameServer {

    private ServerSession session;

    private ServerSocketProperties properties;

    private ServerBootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    private BiConsumer<ProcessEvent, Session> onEvent;

    private AtomicLong openTimestamp = new AtomicLong();

    public NetServer(ServerBootstrap bootstrap, EventLoopGroup eventLoopGroup) {
        this.bootstrap = bootstrap;
        this.eventLoopGroup = eventLoopGroup;
    }

    public void init(ServerSocketProperties properties, BiConsumer<ProcessEvent, Session> onEvent) {
        this.properties = properties;
        this.onEvent = onEvent;
    }

    @Override
    public void asyncStart() {
        try {
            onEvent.accept(ProcessEvent.SERVER_START, session);
            ChannelFuture future = this.bootstrap.bind(properties.getPort());
            this.session = new ServerSession(future.channel());
            this.openTimestamp.set(System.currentTimeMillis());
            startDaemonAwaitThread(future);
            log.info("Start server on {}", this.properties.getPort());
        } catch (Exception e) {
            throw new ServerStarException("Server start error", e);
        }
    }

    @Override
    public void start() {
        try {
            onEvent.accept(ProcessEvent.SERVER_START, session);
            ChannelFuture future = this.bootstrap.bind(properties.getPort())
                    .sync();
            this.session = new ServerSession(future.channel());
            this.openTimestamp.set(System.currentTimeMillis());
            log.info("Start server on {}", this.properties.getPort());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new ServerStarException("Server start error", e);
        }
    }

    @Override
    public boolean isStared() {
        return true;
    }

    @Override
    public boolean shutDown() {
        onEvent.accept(ProcessEvent.CLOSE, session);
        this.eventLoopGroup.shutdownGracefully();
        return true;
    }

    private void startDaemonAwaitThread(ChannelFuture future) {
        Thread awaitThread = new Thread("server") {
            @Override
            public void run() {
                try {
                    future.sync().channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    log.error("", e);
                    System.exit(-1);
                }
            }
        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    @Override
    public int getPort() {
        return properties.getPort();
    }

    static class ServerSession extends AbstractSession {

        protected ServerSession(Channel channel) {
            super(channel, false, null);
        }
    }
}
