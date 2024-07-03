package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.protocol.Response;

import java.util.concurrent.CompletableFuture;

public interface AsyncResponse<T> {

    T get();

    Response getResponse();

    CompletableFuture<Response> getResponseFuture();
}
