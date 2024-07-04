package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.bootstrap.AbstractBootstrap;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.core.process.event.ProcessEventListen;
import com.lzh.game.framework.socket.core.process.impl.FutureResponseProcess;
import com.lzh.game.framework.socket.core.process.impl.RequestFuture;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.codec.GameByteToMessageDecoder;
import com.lzh.game.framework.socket.core.protocol.codec.GameMessageToByteEncoder;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.SessionUtils;
import com.lzh.game.framework.socket.core.session.impl.FutureSession;
import com.lzh.game.framework.socket.utils.Constant;
import com.lzh.game.framework.socket.utils.SocketUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Client
 * Default processor: auth -> request -> response
 *
 * @param <C>
 */
@Slf4j
public class GameTcpClient<C extends GameClientSocketProperties> extends AbstractBootstrap<C>
        implements GameClient {

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private ExecutorService requestService;

    public GameTcpClient(C properties, SessionManage<Session> sessionManage, MessageManager messageManager, InvokeSupport invokeSupport) {
        super(properties, sessionManage, messageManager, invokeSupport);
    }

    public GameTcpClient(C properties) {
        super(properties);
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
        session.write(protocolToRequest(param));
    }

    private Request protocolToRequest(Object param) {
        var anno = param.getClass().getAnnotation(Protocol.class);
        if (Objects.isNull(anno)) {
            throw new RuntimeException("Param didn't has @Protocol");
        }
        short msgId = anno.value();
        if (!getMessageManager().hasMessage(msgId)) {
            getMessageManager().addMessage(param.getClass());
        }
        return SocketUtils.createRequest(msgId, param, Constant.ONEWAY_SIGN);
    }

    private Channel createChannel(String host, int port, int connectTimeout) {
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        ChannelFuture future = this.bootstrap.connect(host, port);
        return future.channel();
    }

    @Override
    public void oneWayRequest(Session session, Request request) {
        checkStatus();
        session.write(request);
    }


    @Override
    public <T> AsyncResponse<T> request(Session session, Request request) {
        checkStatus();
        RequestFuture future = RequestFuture.newFuture(request, getProperties().getConnectTimeout(), requestService);
        AsyncResponse<T> response = new FutureAsyncResponse<>(future);
        session.write(request);
        return response;
    }

    @Override
    public <T> AsyncResponse<T> request(Session session, Object param) {
        return null;
    }

    @Override
    public void oneWayCompose(Session session, short msgId, Object... params) {
        checkStatus();
        var request = SocketUtils.createRequest(msgId, composeProtocol(msgId, params));
        oneWayRequest(session, request);
    }


    @Override
    public <T> AsyncResponse<T> requestCompose(Session session, short msgId, Object... params) {
        checkStatus();
        var request = SocketUtils.createRequest(msgId, composeProtocol(msgId, params));
        return request(session, request);
    }

    private Object composeProtocol(short msgId, Object... params) {
        var message = getMessageManager().findDefine(msgId);
        if (Objects.isNull(message)) {
            getBeanHelper().parseMessage(msgId, params);
            message = getMessageManager().findDefine(msgId);
        }
        if (message.isCompose()) {
            try {
                return message.getAllArgsConstructor().newInstance(params);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (params.length == 0) {
            return null;
        }
        return params[0];
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
                                .addLast("decoder", new GameByteToMessageDecoder(getMessageManager()))
                                .addLast("encoder", new GameMessageToByteEncoder(getMessageManager()))
                                .addLast(getIoHandler());
                    }
                });
        return bootstrap;
    }

    @Override
    protected void doInit(C properties) {
        getPipeline().addProcessEventListen(ProcessEvent.CONNECT, new SessionFutureListen());
        if (Objects.isNull(requestService)) {
            requestService = Executors.newCachedThreadPool();
        }
        if (properties.isUseDefaultProcessor()) {
            getPipeline().addLast(new FutureResponseProcess());
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

    private static class SessionFutureListen implements ProcessEventListen {

        @Override
        public void event(Session session, Object data) {
            var future = SessionUtils.getBindFuture(session.getChannel());
            future.complete(session);
            log.info("session {} created", session.getId());
        }
    }

    public ExecutorService getRequestService() {
        return requestService;
    }

    public void setRequestService(ExecutorService requestService) {
        this.requestService = requestService;
    }
}
