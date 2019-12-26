package com.lzh.game.start.pool;

import com.lzh.game.start.model.world.scene.Scene;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface GameExecutorService {

    <T>CompletableFuture<T> submit(Scene scene, Function<Scene, T> function);

    <T>CompletableFuture<T> submit(Long key, Function<Long, T> function);

}
