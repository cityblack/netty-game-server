package com.lzh.game.start.pool;

import com.lzh.game.framework.socket.core.process.ProcessorExecutorService;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * It's a demo for RequestBusinessPool.
 * If the request is about the player, the player's request should be forwarded to the player's game scene
 */
@Slf4j
public class DefaultBusinessThreadExecutorService implements ProcessorExecutorService, GameExecutorService {

    private static DefaultBusinessThreadExecutorService instance = new DefaultBusinessThreadExecutorService();
    // Use disruptor?
    private static ExecutorService[] executors;

    private final static ExecutorService COMMON = Executors.newSingleThreadScheduledExecutor();

    private static int available;

    public static DefaultBusinessThreadExecutorService getInstance() {
        return instance;
    }

    static {
        init();
    }

    private int index(Request request) {
        Session session = request.getSession();
        // todo. Game scene
        return session.hashCode() % available;
    }

    private int index(Session session) {
        return Math.abs(Objects.hash(session.getId())) % available;
    }

    public static void init() {
        available = Runtime.getRuntime().availableProcessors() * 2;
        executors = IntStream.range(0, available)
                .mapToObj(e -> Executors.newSingleThreadScheduledExecutor())
                .toArray(ExecutorService[]::new);
        log.info("Opens {} core threads..", available);
    }

    @Override
    public <T> CompletableFuture<T> submit(Long key, Function<Long, T> function) {
        int mod = (int) (Math.abs(key) / available);
        return CompletableFuture.supplyAsync(() -> function.apply(key), executors[mod]);
    }

//    @Override
//    public void submit(ServerExchange exchange, Runnable runnable) {
//        if (specialRequest(exchange.getRequest().cmd())) {
//            CompletableFuture.runAsync(runnable, COMMON);
//        } else {
//            CompletableFuture.runAsync(runnable, executors[index(exchange)]);
//        }
//    }
//
//    @Override
//    public void submit(Session session, Runnable runnable) {
//        CompletableFuture.runAsync(runnable, executors[index(session)]);
//    }

    private DefaultBusinessThreadExecutorService() {
    }

    @Override
    public void submit(Session session, Object msg, Runnable runnable) {
        Request request = (Request) msg;
        int index = index(request);
        executors[index].submit(runnable);
    }
}
