package com.lzh.game.framework.socket.core.bootstrap.client;


import com.lzh.game.framework.socket.core.protocol.Response;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FutureAsyncResponse<T> implements AsyncResponse<T> {

    private final CompletableFuture<Response> future;

    private final Class<T> returnType;

    private final int timeout;

    public FutureAsyncResponse(CompletableFuture<Response> future, Class<T> returnType, int timeout) {
        this.future = future;
        this.returnType = returnType;
        this.timeout = timeout;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        Response response = getResponse();
        if (Objects.nonNull(response.getError())) {
            throw new RuntimeException(response.getError());
        }
        if (returnType == Response.class) {
            return (T) response;
        }
        return (T) response.getData();
    }

    @Override
    public Response getResponse() {
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<Response> getResponseFuture() {
        return future;
    }
}
