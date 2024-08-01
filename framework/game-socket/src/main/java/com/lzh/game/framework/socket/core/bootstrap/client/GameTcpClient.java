package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.codec.ByteToGameMessageDecoder;
import com.lzh.game.framework.socket.core.protocol.codec.GameMessageToByteEncoder;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionUtils;
import com.lzh.game.framework.socket.core.session.impl.FutureSession;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.Executors;

/**
 * Client
 * Default processor: auth -> request -> response
 *
 * @param <C>
 */
@Slf4j
public class GameTcpClient<C extends GameClientSocketProperties> extends AbstractClient<C>
        implements GameClient {

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private final DefaultGameRequest gameRequest;

    public GameTcpClient(C properties, BootstrapContext context) {
        super(properties, context);
        this.gameRequest = new DefaultGameRequest(this);
    }

    public GameTcpClient(C properties) {
        super(properties);
        this.gameRequest = new DefaultGameRequest(this);
    }

    @Override
    public Session conn(String host, int port, int connectTimeout) {
        checkStatus();
        var channel = createChannel(host, port, connectTimeout);
        var future = SessionUtils.getBindFuture(channel);
        return new FutureSession(future);
    }

    @Override
    public void oneWay(Session session, Object param) {
        gameRequest.oneWay(session, param);
    }

    private Channel createChannel(String host, int port, int connectTimeout) {
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        ChannelFuture future = this.bootstrap.connect(host, port);
        return future.channel();
    }

    @Override
    public void oneWayRequest(Session session, Request request) {
        gameRequest.oneWayRequest(session, request);
    }

    @Override
    public <T> AsyncResponse<T> request(Session session, Request request, Class<T> type) {
        return gameRequest.request(session, request, type);
    }

    @Override
    public <T> AsyncResponse<T> request(Session session, Object param, Class<T> type) {
        return gameRequest.request(session, param, type);
    }

    @Override
    public void oneWayCompose(Session session, short msgId, Object... params) {
        gameRequest.oneWayCompose(session, msgId, params);
    }

    @Override
    public <T> AsyncResponse<T> requestCompose(Session session, short msgId, Class<T> type, Object... params) {
        return gameRequest.requestCompose(session, msgId, type, params);
    }

    private Bootstrap createBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler(properties.getNetty().getLogLevel()))
                                .addLast("decoder", new ByteToGameMessageDecoder(context, getProperties().isBodyDateToBytes()))
                                .addLast("encoder", new GameMessageToByteEncoder(context))
                                .addLast(getIoHandler());
                    }
                });
        return bootstrap;
    }

    @Override
    protected void doInit(C properties) {
        if (Objects.isNull(getRequestService())) {
            setRequestService(Executors.newCachedThreadPool());
        }
        group = new NioEventLoopGroup();
        this.bootstrap = createBootstrap();
    }

    @Override
    protected void startup() {
        this.bootstrap = createBootstrap();
    }

    @Override
    protected void asyncStartup() {
        this.bootstrap = createBootstrap();
    }

    private void checkStatus() {
        if (!isStared()) {
            throw new RuntimeException("Client is not started..");
        }
    }
}
