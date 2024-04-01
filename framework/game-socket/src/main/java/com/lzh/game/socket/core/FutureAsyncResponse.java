package com.lzh.game.socket.core;

import com.lzh.game.common.serialization.ProtoBufUtils;
import com.lzh.game.socket.Response;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class FutureAsyncResponse<T> implements AsyncResponse<T> {

    private CompletableFuture<Response> future;

    private Class<T> type;

    public FutureAsyncResponse(CompletableFuture<Response> future, Class<T> type) {
        this.future = future;
        this.type = type;
    }

    @Override
    public T get() {
        Response response = getResponse();
        if (Objects.isNull(response.byteData())) {
            return null;
        }
        return ProtoBufUtils.deSerialize(response.byteData(), type);
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
