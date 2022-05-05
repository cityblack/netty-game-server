package com.lzh.game.client.bootstrap;

import com.lzh.game.socket.GameSocketProperties;
import com.lzh.game.socket.core.bootstrap.AbstractBootstrap;
import com.lzh.game.socket.core.coder.GameByteToMessageDecoder;
import com.lzh.game.socket.core.coder.GameMessageToMessageDecoder;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GameClientBootstrap extends AbstractBootstrap<GameSocketProperties>
        implements TcpClient {

    public final static AttributeKey<CompletableFuture<ClientGameSession>> SESSION_FUTURE = AttributeKey.newInstance("session.future");

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    public GameClientBootstrap(SessionManage<ClientGameSession> sessionManage, GameSocketProperties properties) {
        super(properties, sessionManage);
    }

    private void addConnectedFuture() {
        SessionManage<ClientGameSession> manage = getSessionManage();
        manage.addConnectListener(clientGameSession -> bindConnectFuture(clientGameSession.getChannel(), true));
    }

    private CompletableFuture<ClientGameSession> bindConnectFuture(Channel channel, boolean connected) {
        CompletableFuture<ClientGameSession> future = new CompletableFuture<>();
        if (!channel.attr(SESSION_FUTURE).compareAndSet(null, future)) {
            future = channel.attr(SESSION_FUTURE).get();
        }
        if (connected) {
            future.complete(getSessionManage().getSession(channel));
        }
        return future;
    }

    @Override
    public Session conn(String host, int port, long connectTimeout) {
        Bootstrap bootstrap = createBootstrap();
        try {
            Channel channel = bootstrap.connect(host, port).channel();
            final CompletableFuture<ClientGameSession> completableFuture = bindConnectFuture(channel, false);
            return completableFuture.get(connectTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SessionManage<ClientGameSession> getSessionManage() {
        return super.getSessionManage();
    }

    private Bootstrap createBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(0, 0, 180, TimeUnit.SECONDS))
                                .addLast("encoder", new GameByteToMessageDecoder())
                                .addLast("decoder", new GameMessageToMessageDecoder())
                                .addLast(getIoHandler());
                    }
                });
        return bootstrap;
    }

    @Override
    protected void doInit(GameSocketProperties properties) {
        group = new NioEventLoopGroup();
    }

    @Override
    protected void startup() {
        this.bootstrap = createBootstrap();
        this.addConnectedFuture();
    }

    @Override
    protected void asyncStartup() {
        this.bootstrap = createBootstrap();
        this.addConnectedFuture();
    }
}
