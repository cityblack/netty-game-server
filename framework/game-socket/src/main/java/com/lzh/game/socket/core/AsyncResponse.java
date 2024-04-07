package com.lzh.game.socket.core;

import java.util.concurrent.CompletableFuture;

public interface AsyncResponse<T> {

    T get();

    Response getResponse();

    CompletableFuture<Response> getResponseFuture();
}
