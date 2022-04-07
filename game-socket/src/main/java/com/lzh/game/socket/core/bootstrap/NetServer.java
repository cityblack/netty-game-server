package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.common.server.ServerCloseEvent;
import com.lzh.game.common.server.ServerStartEvent;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.exception.ServerStarException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@Slf4j
public class NetServer implements GameServer {

    private Channel channel;

    private int port;

    private ServerBootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    private AtomicLong openTimestamp = new AtomicLong();

    public NetServer(int port) {
        this.port = port;
    }

    @Override
    public void asyncStart() {
        try {
            sendBeforeStartEvent(port);
            ChannelFuture future = this.bootstrap.bind(port);
            this.channel = future.channel();
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
            ChannelFuture future = this.bootstrap.bind(port).sync().channel().closeFuture().sync();
            this.channel = future.channel();
            this.openTimestamp.set(System.currentTimeMillis());
            log.info("Start server on {}", this.port);
        } catch (Exception e) {
            throw new ServerStarException("Server start error", e);
        }
    }

    @Override
    public void stop() {
        sendCloseEvent();
        this.eventLoopGroup.shutdownGracefully();
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
        EventBus.getDefault().post(new ServerStartEvent(System.currentTimeMillis(), port));
    }

    private void sendCloseEvent() {
        try {
            EventBus.getDefault().post(new ServerCloseEvent(System.currentTimeMillis(), port));
        } catch (Exception e) {
            log.error("Close server error: ", e);
        }
    }
}
