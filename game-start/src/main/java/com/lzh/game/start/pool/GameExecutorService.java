package com.lzh.game.start.pool;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface GameExecutorService {

    <T>CompletableFuture<T> submit(Long key, Function<Long, T> function);

}
