package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.bootstrap.AbstractBootstrap;
import com.lzh.game.framework.socket.utils.Constant;
import com.lzh.game.framework.socket.GameServerSocketProperties;
import com.lzh.game.framework.socket.GameSocketProperties;
import com.lzh.game.framework.socket.core.AsyncResponse;
import com.lzh.game.framework.socket.core.FutureAsyncResponse;
import com.lzh.game.framework.socket.core.process.impl.RequestFuture;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.SessionUtils;
import com.lzh.game.framework.socket.core.session.impl.FutureSession;
import com.lzh.game.framework.socket.utils.SocketUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GameTcpClient extends AbstractBootstrap<GameSocketProperties>
        implements GameClient {

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private ExecutorService service;

    public GameTcpClient() {
        this(new GameServerSocketProperties());
    }

    public GameTcpClient(GameSocketProperties properties) {
        this(properties, defaultSession(), defaultExecutor());
    }

    public GameTcpClient(GameSocketProperties properties, SessionManage<Session> sessionManage) {
        this(properties, sessionManage, defaultExecutor());
    }

    public GameTcpClient(GameSocketProperties properties, SessionManage<Session> sessionManage, ExecutorService service) {
        super(properties, sessionManage);
        this.service = service;
    }

    public static ExecutorService defaultExecutor() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public Session conn(String host, int port, int connectTimeout) {
        Future<Session> future = SessionUtils.getBindFuture(createChannel(host, port, connectTimeout));
        return new FutureSession(future);
    }

    private Channel createChannel(String host, int port, int connectTimeout) {
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        ChannelFuture future = this.bootstrap.connect(host, port);
        future.addListeners(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("connect to [{}:{}]", host, port);
                } else {
                    log.error("connect to [{}:{}] fail!!!", host, port);
                }
            }
        });
        return future.channel();
    }

    @Override
    public void oneWay(Session session, Request request) {
        checkStatus();
        session.write(request);
    }

    @Override
    public void oneWay(Session session, short cmd, Object params) {
        checkStatus();
        oneWay(session, SocketUtils.createRequest(cmd, params, Constant.ONEWAY_SIGN));
    }

    @Override
    public <T>AsyncResponse<T> request(Session session, Request request, Class<T> clazz) {
        checkStatus();
        RequestFuture future = RequestFuture.newFuture(request, getProperties().getRequestTimeout(), service);
        AsyncResponse<T> response = new FutureAsyncResponse<>(future, clazz);
        session.write(request);
        return response;
    }

    @Override
     public <T> AsyncResponse<T> request(Session session, short cmd, Object params, Class<T> returnType) {
        return request(session, SocketUtils.createRequest(cmd, params, Constant.REQUEST_SIGN), returnType);
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
//                                .addLast(new ProtobufVarint32FrameDecoder())
//                                .addLast("decoder", new GameByteToMessageDecoder())
//                                .addLast(new ProtobufVarint32LengthFieldPrepender())
//                                .addLast("encoder", new GameMessageToByteDecoder())
                                .addLast(getIoHandler());
                    }
                });
        return bootstrap;
    }

    @Override
    protected void doInit(GameSocketProperties properties) {
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

    public ExecutorService getService() {
        return service;
    }

    private void checkStatus() {
        if (!isStared()) {
            throw new RuntimeException("Client is not started..");
        }
    }
}
