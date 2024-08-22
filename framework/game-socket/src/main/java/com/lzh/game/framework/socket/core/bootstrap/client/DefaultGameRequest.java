package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.process.impl.RequestFuture;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.utils.Constant;
import com.lzh.game.framework.socket.utils.SocketUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Client Request impl
 *
 * @author zehong.l
 * @since 2024-07-30 15:41
 **/
@Slf4j
public class DefaultGameRequest implements GameClient {

    private final AbstractClient<?> client;

    public DefaultGameRequest(AbstractClient<?> client) {
        this.client = client;
    }

    @Override
    public void start() {
        client.start();
    }

    @Override
    public boolean isStared() {
        return client.isStared();
    }

    @Override
    public void shutDown() {
        client.shutDown();
    }

    @Override
    public void asyncStart() {
        client.asyncStart();
    }

    @Override
    public Session conn(String host, int port, int connectTimeout) {
        return client.conn(host, port, connectTimeout);
    }

    @Override
    public void oneWay(Session session, Object param) {
        session.write(protocolToRequest(param, Constant.ONEWAY_SIGN))
                .addListener(new WritePromise());
    }

    private Request protocolToRequest(Object param, byte type) {
        var anno = param.getClass().getAnnotation(Protocol.class);
        if (Objects.isNull(anno)) {
            throw new RuntimeException("Param didn't has @Protocol");
        }
        short msgId = anno.value();
        return SocketUtils.createRequest(msgId, param, type);
    }

    @Override
    public void oneWayRequest(Session session, Request request) {
        checkStatus();
        session.write(request);
    }

    @Override
    public <T> AsyncResponse<T> request(Session session, Request request, Class<T> type) {
        checkStatus();
        checkReturnType(type);
        RequestFuture future = RequestFuture.newFuture(request, client.getProperties().getRequestTimeout(), client.getRequestService());
        AsyncResponse<T> response = new FutureAsyncResponse<>(future);
        session.write(request).addListener(new WritePromise(future));
        return response;
    }

    // Must be @Protocol
    private void checkReturnType(Class<?> type) {
        if (!type.isAnnotationPresent(Protocol.class)) {
            throw new IllegalArgumentException("Not support the return type:" + type.getName());
        }
    }

    @Override
    public <T> AsyncResponse<T> request(Session session, Object param, Class<T> type) {
        checkStatus();
        return request(session, protocolToRequest(param, Constant.REQUEST_SIGN), type);
    }

    private void checkStatus() {
        if (!isStared()) {
            throw new RuntimeException("Client is not started..");
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class WritePromise implements ChannelFutureListener {

        private RequestFuture requestFuture;

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                return;
            }
            if (Objects.nonNull(future.cause())) {
                if (Objects.nonNull(requestFuture)) {
                    RequestFuture.requestError(requestFuture, future.cause());
                } else {
                    log.error("", future.cause());
                }
            }
        }
    }
}
