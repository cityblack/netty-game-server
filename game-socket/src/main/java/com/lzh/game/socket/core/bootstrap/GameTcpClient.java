package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.*;
import com.lzh.game.socket.core.AsyncResponse;
import com.lzh.game.socket.core.FutureAsyncResponse;
import com.lzh.game.socket.core.RequestFuture;
import com.lzh.game.socket.core.coder.GameByteToMessageDecoder;
import com.lzh.game.socket.core.coder.GameMessageToMessageDecoder;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import java.util.concurrent.*;

public class GameTcpClient extends AbstractBootstrap<GameSocketProperties>
        implements GameClient {

    public final static AttributeKey<CompletableFuture<Session>> SESSION_FUTURE = AttributeKey.newInstance("session.future");

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private ExecutorService service;

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

    private void addConnectedFuture() {
        SessionManage<Session> manage = getSessionManage();
        manage.addConnectListener(clientGameSession -> bindConnectFuture(clientGameSession.getChannel(), true));
    }

    private CompletableFuture<Session> bindConnectFuture(Channel channel, boolean connected) {
        CompletableFuture<Session> future = new CompletableFuture<>();
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
            final CompletableFuture<Session> completableFuture = bindConnectFuture(channel, false);
            return completableFuture.get(connectTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void oneWay(Session session, Request request) {
        session.write(request);
    }

    @Override
    public void oneWay(Session session, int commandKey, int cmd, Object params) {
        session.write(SocketUtils.createRequest(commandKey, cmd, params, Constant.ONEWAY_SIGN));
    }

    @Override
    public <T> AsyncResponse<T> request(Session session, int commandKey, int cmd, Object params, Class<T> clazz) {
        return request(session, SocketUtils.createRequest(commandKey, cmd, params), clazz);
    }

    @Override
    public <T>AsyncResponse<T> request(Session session, Request request, Class<T> clazz) {
        RequestFuture future = RequestFuture.newFuture(request, getProperties().getRequestTimeout(), service);
        AsyncResponse<T> response = new FutureAsyncResponse<>(future, clazz);
        session.write(request);
        return response;
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
