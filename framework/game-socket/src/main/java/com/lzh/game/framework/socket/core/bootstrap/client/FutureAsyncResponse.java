package com.lzh.game.framework.socket.core.bootstrap.client;


import com.lzh.game.framework.socket.core.protocol.Response;
import com.lzh.game.framework.socket.exception.EncodeSerializeException;
import io.netty.handler.codec.EncoderException;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class FutureAsyncResponse<T> implements AsyncResponse<T> {

    private CompletableFuture<Response> future;

    public FutureAsyncResponse(CompletableFuture<Response> future) {
        this.future = future;
    }

    @Override
    public T get() {
        Response response = getResponse();
        if (Objects.nonNull(response.getError())) {
            throw new RuntimeException(response.getError());
        }
        return (T) response.getData();
    }

    @Override
    public Response getResponse() {
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<Response> getResponseFuture() {
        return future;
    }
}
