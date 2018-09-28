package com.lzh.netty.socket.dispatcher;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public interface TaskFactory {

    <T,R>CompletableFuture<R> addTask(T value, Function<T,R> function);

    <T>CompletableFuture<T> addRequestTask(ServerExchange exchange, Function<ServerExchange, T> function);

    Future<?> addTask(Runnable runnable);

    Future<?> addDelayTask(Runnable runnable, long time, TimeUnit timeUnit);

    Integer getTaskAllSize();
}
