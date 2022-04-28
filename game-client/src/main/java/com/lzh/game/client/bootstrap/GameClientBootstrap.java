package com.lzh.game.client.bootstrap;

import com.lzh.game.common.scoket.AbstractBootstrap;
import com.lzh.game.common.scoket.GameIoHandler;
import com.lzh.game.common.scoket.GameSocketProperties;
import com.lzh.game.common.scoket.MessageHandler;
import com.lzh.game.socket.core.coder.GameByteToMessageDecoder;
import com.lzh.game.socket.core.coder.GameMessageToMessageDecoder;
import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.common.scoket.session.SessionManage;
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

    public final static AttributeKey<CompletableFuture<Session>> SESSION_FUTURE = AttributeKey.newInstance("session.future");

    private EventLoopGroup group;

    private SessionManage<ClientGameSession> sessionManage;

    public GameClientBootstrap(MessageHandler handler, SessionManage<ClientGameSession> sessionManage, GameSocketProperties properties) {
        super(properties, handler);
        this.sessionManage = sessionManage;
    }

    @Override
    public Session conn(String host, int port, long connectTimeout) {
        Bootstrap bootstrap = createBootstrap();
        try {
            Channel channel = bootstrap.connect(host, port).sync().channel();
            CompletableFuture<Session> future = new CompletableFuture<>();
            if (channel.attr(SESSION_FUTURE).compareAndSet(null, future)) {
                return future.get(connectTimeout, TimeUnit.MILLISECONDS);
            } else {
                return channel.attr(SESSION_FUTURE).get().get();
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SessionManage<ClientGameSession> sessionManage() {
        return sessionManage;
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
                                .addLast("decoder",new GameMessageToMessageDecoder())
                                .addLast(clientHandler());
                    }
                });
        return bootstrap;
    }

    private GameIoHandler<ClientGameSession> clientHandler() {
        return new GameIoHandler<>(handler, sessionManage);
    }

    @Override
    protected void init(GameSocketProperties properties) {
        group = new NioEventLoopGroup();
    }
}
