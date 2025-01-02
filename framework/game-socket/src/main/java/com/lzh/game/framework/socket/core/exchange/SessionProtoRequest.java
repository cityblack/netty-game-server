package com.lzh.game.framework.socket.core.exchange;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.FutureAsyncResponse;
import com.lzh.game.framework.socket.core.process.impl.RequestFuture;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.Constant;
import com.lzh.game.framework.socket.utils.SocketUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Request impl
 *
 * @author zehong.l
 * @since 2024-07-30 15:41
 **/
@Slf4j
public class SessionProtoRequest implements ProtoRequest {

    private final Session session;

    private final BootstrapContext<?> context;

    public SessionProtoRequest(Session session, BootstrapContext<?> context) {
        this.session = session;
        this.context = context;
    }

    @Override
    public void oneWay(Object param) {
        if (param instanceof Request request) {
            oneWayRequest(request);
        } else {
            session.write(protocolToRequest(param, Constant.ONEWAY_SIGN));
        }
    }

    private Request protocolToRequest(Object param, byte type) {
        var defined = context.getMessageManager()
                .findDefine(param.getClass());
        if (Objects.isNull(defined)) {
            throw new RuntimeException("Param didn't has @Protocol");
        }
        return SocketUtils.createRequest(defined.getMsgId(), param, type);
    }

    public void oneWayRequest(Request request) {
        session.write(request);
    }

    public <T> AsyncResponse<T> protoRequest(Request request, Class<T> returnType, ExecutorService service) {
        var timeout = context.getProperties().getRequestTimeout();
        RequestFuture future = RequestFuture.newFuture(request, timeout, service);
        AsyncResponse<T> response = new FutureAsyncResponse<>(future, returnType, timeout);
        session.write(request, true).addListener(new WritePromise(future));
        return response;
    }

    @Override
    public <T> AsyncResponse<T> request(Object param, Class<T> returnType, ExecutorService service) {
        if (param instanceof Request request) {
            return protoRequest(request, returnType, service);
        }
        return protoRequest(protocolToRequest(param, Constant.REQUEST_SIGN), returnType, service);
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
                }
                log.error("", future.cause());
            }
        }
    }
}
