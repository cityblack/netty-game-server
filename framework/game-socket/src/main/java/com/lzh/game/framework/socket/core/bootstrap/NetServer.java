package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServer;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.core.session.impl.AbstractSession;
import com.lzh.game.framework.socket.exception.ServerStarException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@Slf4j
public class NetServer implements GameServer {

    private ServerSession session;

    private ProcessorPipeline pipeline;

    private GameServerSocketProperties properties;

    private int port;

    private ServerBootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    private AtomicLong openTimestamp = new AtomicLong();

    public NetServer(int port, GameServerSocketProperties properties, ProcessorPipeline pipeline) {
        this.port = port;
        this.properties = properties;
        this.pipeline = pipeline;
    }

    @Override
    public void asyncStart() {
        try {
            sendBeforeStartEvent(port);
            ChannelFuture future = this.bootstrap.bind(port);
            this.session = new ServerSession(future.channel());
            this.openTimestamp.set(System.currentTimeMillis());
            startDaemonAwaitThread(future);
            log.info("Start server on {}", this.port);
        } catch (Exception e) {
            throw new ServerStarException("Server start error", e);
        }
    }

    @Override
    public void start() {
        try {
            sendBeforeStartEvent(port);
            ChannelFuture future = this.bootstrap.bind(port)
                    .sync();
            this.session = new ServerSession(future.channel());
            this.openTimestamp.set(System.currentTimeMillis());
            log.info("Start server on {}", this.port);
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
        sendCloseEvent();
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

    private void sendBeforeStartEvent(int port) {
        pipeline.fireEvent(ProcessEvent.SERVER_START, session, port);
    }

    private void sendCloseEvent() {
        pipeline.fireEvent(ProcessEvent.CLOSE, session);
    }

    static class ServerSession extends AbstractSession {

        protected ServerSession(Channel channel) {
            super(channel, false);
        }
    }
}
