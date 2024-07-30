package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.process.impl.RequestFuture;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.utils.Constant;
import com.lzh.game.framework.socket.utils.SocketUtils;

import java.util.Objects;

/**
 * Client Request impl
 * @author zehong.l
 * @since 2024-07-30 15:41
 **/
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
        session.write(protocolToRequest(param, Constant.ONEWAY_SIGN));
    }

    private Request protocolToRequest(Object param, byte type) {
        var context = client.getContext();
        var anno = param.getClass().getAnnotation(Protocol.class);
        if (Objects.isNull(anno)) {
            throw new RuntimeException("Param didn't has @Protocol");
        }
        short msgId = anno.value();
        if (!context.getMessageManager().hasMessage(msgId)) {
            context.getMessageManager().addMessage(param.getClass());
        }
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
        session.write(request);
        return response;
    }

    // Must be @Protocol or default type
    private void checkReturnType(Class<?> type) {
        var context = client.getContext();
        if (type.isAnnotationPresent(Protocol.class)) {
            var msgId = type.getAnnotation(Protocol.class);
            if (!context.getMessageManager().hasMessage(msgId.value())) {
                context.getMessageManager().addMessage(type);
            }
        } else {
            if (Objects.isNull(context.getMessageManager().findDefaultDefined(type))) {
                throw new IllegalArgumentException("Not support the return type:" + type.getName());
            }
        }
    }

    @Override
    public <T> AsyncResponse<T> request(Session session, Object param, Class<T> type) {
        return request(session, protocolToRequest(param, Constant.REQUEST_SIGN), type);
    }

    @Override
    public void oneWayCompose(Session session, short msgId, Object... params) {
        checkStatus();
        var request = SocketUtils.createRequest(msgId, composeProtocol(msgId, params));
        oneWayRequest(session, request);
    }


    @Override
    public <T> AsyncResponse<T> requestCompose(Session session, short msgId, Class<T> type, Object... params) {
        checkStatus();
        var request = SocketUtils.createRequest(msgId, composeProtocol(msgId, params));
        return request(session, request, type);
    }

    private Object composeProtocol(short msgId, Object... params) {
        var context = client.getContext();
        var message = context.getMessageManager().findDefine(msgId);
        if (Objects.isNull(message)) {
            context.getBeanHelper().parseMessage(msgId, params);
            message = context.getMessageManager().findDefine(msgId);
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

    private void checkStatus() {
        if (!isStared()) {
            throw new RuntimeException("Client is not started..");
        }
    }
}
