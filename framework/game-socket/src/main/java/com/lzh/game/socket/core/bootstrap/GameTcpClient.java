package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.*;
import com.lzh.game.socket.core.AsyncResponse;
import com.lzh.game.socket.core.FutureAsyncResponse;
import com.lzh.game.socket.core.RequestFuture;
import com.lzh.game.socket.core.protocol.codec.GameByteToMessageDecoder;
import com.lzh.game.socket.core.protocol.codec.GameMessageToMessageDecoder;
import com.lzh.game.socket.core.session.FutureSession;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;
import com.lzh.game.socket.core.session.SessionUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
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
        /*future.awaitUninterruptibly();
        if (!future.isDone()) {
            String errMsg = "Create connection to " + host + ":" + port + " timeout!";
            throw new RuntimeException(errMsg);
        }
        if (future.isCancelled()) {
            String errMsg = "Create connection to " + host + ":" + port + " cancelled by user!";
            log.warn(errMsg);
            throw new RuntimeException(errMsg);
        }
        if (!future.isSuccess()) {
            String errMsg = "Create connection to " + host + ":" + port + " error!";
            log.warn(errMsg);
            throw new ConnectException(errMsg);
        }*/
        return future.channel();
    }

    @Override
    public void oneWay(Session session, Request request) {
        checkStatus();
        session.write(request);
    }

    @Override
    public void oneWay(Session session, int cmd, Object params) {
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
    public <T> AsyncResponse<T> request(Session session, int cmd, Object params, Class<T> returnType) {
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
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast("decoder", new GameByteToMessageDecoder())
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast("encoder", new GameMessageToMessageDecoder())
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
