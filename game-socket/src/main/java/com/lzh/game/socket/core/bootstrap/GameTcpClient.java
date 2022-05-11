package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.GameClient;
import com.lzh.game.socket.GameSocketProperties;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.SocketUtils;
import com.lzh.game.socket.core.AsyncResponse;
import com.lzh.game.socket.core.FutureAsyncResponse;
import com.lzh.game.socket.core.RequestFuture;
import com.lzh.game.socket.core.coder.GameByteToMessageDecoder;
import com.lzh.game.socket.core.coder.GameMessageToMessageDecoder;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;
import com.lzh.game.socket.core.session.SessionUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

@Slf4j
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
            future.complete(SessionUtils.channelGetSession(channel));
        }
        return future;
    }

    @Override
    public Session conn(String host, int port, long connectTimeout) {
        checkStatus();
        FutureSession session = new FutureSession();
        session.connect(host, port, connectTimeout);
        return session;
    }

    @Override
    public void oneWay(Session session, Request request) {
        checkStatus();
        session.write(request);
    }

    @Override
    public void oneWay(Session session, int commandKey, int cmd, Object params) {
        checkStatus();
        session.write(SocketUtils.createRequest(commandKey, cmd, params, Constant.ONEWAY_SIGN));
    }

    @Override
    public <T> AsyncResponse<T> request(Session session, int commandKey, int cmd, Object params, Class<T> clazz) {
        checkStatus();
        return request(session, SocketUtils.createRequest(commandKey, cmd, params), clazz);
    }

    @Override
    public <T>AsyncResponse<T> request(Session session, Request request, Class<T> clazz) {
        checkStatus();
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

    class FutureSession implements Session {

        private CompletableFuture<Session> future;

        public void connect(String host, int port, long connectTimeout) {
            Bootstrap bootstrap = createBootstrap();
            Channel channel = bootstrap.connect(host, port).channel();
            this.future = bindConnectFuture(channel, false);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    FutureSession.this.timeout(host, port, connectTimeout);
                }
            }, connectTimeout);
        }

        public void timeout(String host, int port, long connectTimeout) {
            if (future.isDone() || future.isCancelled()) {
                return;
            }
            log.warn("Connect [{}:{}] timeout!! timeout:{}", host, port, connectTimeout);
            future.cancel(true);
            future.completeExceptionally(new TimeoutException("Client connect timeout!!"));
        }

        @Override
        public String getRemoteAddress() {
            return getSession().getRemoteAddress();
        }

        @Override
        public String getId() {
            return getSession().getId();
        }

        @Override
        public Instant getCreationTime() {
            return getSession().getCreationTime();
        }

        @Override
        public Instant getLastAccessTime() {
            return getSession().getLastAccessTime();
        }

        @Override
        public void updateLastAccessTime() {
            getSession().updateLastAccessTime();
        }

        @Override
        public Map<String, Object> getAttributes() {
            return getSession().getAttributes();
        }

        @Override
        public void setAttribute(String attributeKey, Object attributeValue) {
            getSession().setAttribute(attributeKey, attributeValue);
        }

        @Override
        public boolean opened() {
            return getSession().opened();
        }

        @Override
        public void close() {
            getSession().close();
        }

        @Override
        public void write(Object data) {
            getSession().write(data);
        }

        @Override
        public Integer getPort() {
            return getSession().getPort();
        }

        @Override
        public Channel getChannel() {
            return getSession().getChannel();
        }

        private Session getSession() {
            try {
                return this.future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ExecutorService getService() {
        return service;
    }

    private void checkStatus() {
        if (isStared()) {
            throw new RuntimeException("Client is not started..");
        }
    }
}
