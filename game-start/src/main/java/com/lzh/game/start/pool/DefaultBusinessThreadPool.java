package com.lzh.game.start.pool;

import com.lzh.game.socket.core.RequestBusinessPool;
import com.lzh.game.socket.core.ServerExchange;
import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.start.cmd.CmdMessage;
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
public class DefaultBusinessThreadPool implements RequestBusinessPool, GameExecutorService {

    private static DefaultBusinessThreadPool instance = new DefaultBusinessThreadPool();
    // Use disruptor?
    private static ExecutorService[] executors;

    private final static ExecutorService COMMON = Executors.newSingleThreadScheduledExecutor();

    private static int available;

    private static Set<Integer> specialCmd;

    public static DefaultBusinessThreadPool getInstance() {
        return instance;
    }

    static {
        specialCmd = new HashSet<>();
        specialCmd.add(CmdMessage.CM_LOGIN);
        specialCmd.add(CmdMessage.CM_REGISTER);
        specialCmd.add(CmdMessage.CM_LOGOUT);
        init();
    }

    private boolean specialRequest(int cmd) {
        return specialCmd.contains(cmd);
    }

    private int index(ServerExchange exchange) {
        Session session = exchange.getSession();
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

    @Override
    public void submit(ServerExchange exchange, Runnable runnable) {
        if (specialRequest(exchange.getRequest().cmd())) {
            CompletableFuture.runAsync(runnable, COMMON);
        } else {
            CompletableFuture.runAsync(runnable, executors[index(exchange)]);
        }
    }

    @Override
    public void submit(Session session, Runnable runnable) {
        CompletableFuture.runAsync(runnable, executors[index(session)]);
    }

    private DefaultBusinessThreadPool() {
    }
}
