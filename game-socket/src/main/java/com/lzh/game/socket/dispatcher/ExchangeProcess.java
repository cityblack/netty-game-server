package com.lzh.game.socket.dispatcher;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 请求线程池处理 不同游戏业务 可能采取的方式不同
 */
public interface ExchangeProcess {

    <T>CompletableFuture<T> addRequestProcess(ServerExchange exchange, Function<ServerExchange, T> function);

}
