package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.bootstrap.AbstractBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.protocol.AuthProtocol;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionUtils;
import com.lzh.game.framework.socket.core.session.impl.AbstractSession;
import com.lzh.game.framework.socket.core.session.impl.FutureSession;
import com.lzh.game.framework.socket.core.session.monitor.ConnectMonitor;
import com.lzh.game.framework.socket.core.session.monitor.DefaultConnectMonitor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * @author zehong.l
 * @since 2024-07-22 17:40
 **/
@Slf4j
public abstract class AbstractClient<C extends GameClientSocketProperties>
        extends AbstractBootstrap<C> implements GameClient {

    private ConnectMonitor monitor;

    private ExecutorService requestService;

    private DefaultGameRequest gameRequest;

    private Bootstrap bootstrap;

    private final AuthProtocol authProtocol;

    private final ChannelFutureListener CONNECTED_AUTH = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            var ch = future.channel();
            // send auth protocol
            oneWay(new AuthSession(ch), authProtocol);
            if (log.isInfoEnabled()) {
                log.info("{} send auth protocol", ch.id().asLongText());
            }
        }
    };

    public AbstractClient(BootstrapContext<C> context) {
        super(context);
        authProtocol = AuthProtocol.of(context.getProperties().getAuthSlot());
    }

    @Override
    protected void init() {
        super.init();
        this.monitor = new DefaultConnectMonitor(getContext().getSessionManage());
        this.gameRequest = new DefaultGameRequest(this);
    }

    public ConnectMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(ConnectMonitor monitor) {
        this.monitor = monitor;
    }

    public ExecutorService getRequestService() {
        return requestService;
    }

    public void setRequestService(ExecutorService requestService) {
        this.requestService = requestService;
    }

    @Override
    public void shutDown() {
        super.shutDown();
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

    protected void checkStatus() {
        if (!isStared()) {
            throw new RuntimeException("Client is not started..");
        }
    }

    protected Channel createChannel(String host, int port, int connectTimeout) {
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        ChannelFuture future = this.bootstrap.connect(host, port);
        future.addListener(CONNECTED_AUTH);
        return future.channel();
    }

    @Override
    protected void startup() {
        this.bootstrap = createBootstrap();
    }

    @Override
    protected void asyncStartup() {
        this.bootstrap = createBootstrap();
    }

    protected abstract Bootstrap createBootstrap();

    private static class AuthSession extends AbstractSession {

        protected AuthSession(Channel channel) {
            super(channel);
        }
    }
}
