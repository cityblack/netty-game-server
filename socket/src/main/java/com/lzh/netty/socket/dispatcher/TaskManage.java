package com.lzh.netty.socket.dispatcher;

import com.lzh.netty.socket.protocol.GameRequest;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 任务管理中心
 */
@Slf4j
public class TaskManage implements TaskFactory, InitializingBean, DisposableBean {
    /**
     * According to different scenarios assigned to different thread pool
     */
    private final static List<ExecutorService> SCENE_POOl = new ArrayList<>();

    private final static Integer POOL_EXECUTOR_NUM = 5;

    private final static ExecutorService TASK_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void afterPropertiesSet() throws Exception {
        final Lock lock = new ReentrantLock();
        lock.lock();
        try {

            poolExecutorList(POOL_EXECUTOR_NUM)
                    .forEach(e -> SCENE_POOl.add(Executors.newSingleThreadExecutor()));
        } finally {
            lock.unlock();
        }
    }

    private Stream<Integer> poolExecutorList(int count) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(i);
        }
        return list.stream();
    }

    private Integer index(int scene) {
        int code = Objects.hashCode(scene);

        return code % POOL_EXECUTOR_NUM;
    }

    private <T>CompletableFuture<T> addTask(Supplier<T> supplier, ExecutorService service) {
        return CompletableFuture.supplyAsync(supplier,service);
    }

    @Override
    public <T, R> CompletableFuture<R> addTask(T value, Function<T, R> function) {
        return addTask(() -> function.apply(value), TASK_POOL);
    }

    @Override
    public <T> CompletableFuture<T> addRequestTask(ServerExchange exchange, Function<ServerExchange, T> function) {
        GameRequest request = (GameRequest) exchange.getRequest();
        /*Integer index = index(request.scene());*/

        return addTask(() -> function.apply(exchange), SCENE_POOl.get(0));

    }

    @Override
    public Future<?> addTask(Runnable runnable) {
        return TASK_POOL.submit(runnable);
    }

    @Override
    public Future<?> addDelayTask(Runnable runnable, long time, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Integer getTaskAllSize() {
        return null;
    }

    @Override
    public void destroy() throws Exception {
        Flux.fromIterable(SCENE_POOl)
                .concatWith(Mono.just(TASK_POOL))
                .switchMap(data -> Flux.fromIterable(data.shutdownNow()))
                .subscribe(data -> log.error("Has not been performed tasks {}", data));
    }
}
