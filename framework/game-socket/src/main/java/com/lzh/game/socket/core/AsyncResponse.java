package com.lzh.game.socket.core;

import com.lzh.game.socket.core.protocol.Response;

import java.util.concurrent.CompletableFuture;

public interface AsyncResponse<T> {

    T get();

    Response getResponse();

    CompletableFuture<Response> getResponseFuture();
}
