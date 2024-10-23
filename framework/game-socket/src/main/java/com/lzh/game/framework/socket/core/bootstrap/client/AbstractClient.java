package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.bootstrap.AbstractBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.heartbeat.HeartbeatTimerTask;
import com.lzh.game.framework.socket.core.protocol.AuthProtocol;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionEvent;
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

    private Bootstrap bootstrap;

    private final AuthProtocol authProtocol;

    private final ChannelFutureListener CONNECTED_AUTH = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            var ch = future.channel();
            if (ch.isActive() && ch.isOpen()) {
                // send auth protocol
                new AuthSession(ch, context).oneWay(authProtocol);
                if (log.isDebugEnabled()) {
                    log.debug("{} send auth protocol", ch.id().asLongText());
                }
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
        var sessionManager = getContext().getSessionManage();
        this.monitor = new DefaultConnectMonitor(sessionManager);
        sessionManager
                .addListener(SessionEvent.CONNECT, (session, o) -> HeartbeatTimerTask.newTimerTask(session, context.getProperties().getHeartbeatInterval()));
        sessionManager
                .addListener(SessionEvent.CLOSE, (session, o) -> HeartbeatTimerTask.removeTimerTask(session));
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
    public Session conn(String host, int port, int connectTimeout) {
        checkStatus();
        var channel = createChannel(host, port, connectTimeout);
        var future = SessionUtils.getBindFuture(channel);
        return new FutureSession(future);
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

    protected void checkStatus() {
        if (!isStared()) {
            throw new RuntimeException("The client has not been started yet.");
        }
    }

    protected abstract Bootstrap createBootstrap();

    private static class AuthSession extends AbstractSession {

        public AuthSession(Channel channel, BootstrapContext<?> context) {
            super(channel, context);
        }
    }
}
