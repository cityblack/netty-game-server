package com.lzh.game.framework.socket.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zehong.l
 * @since 2024-10-22 18:31
 **/
@Slf4j
public class ShutdownHook {

    private static final List<Runnable> SHUTDOWN_TASK = new CopyOnWriteArrayList<>();

    public static void register(Runnable runnable) {
        SHUTDOWN_TASK.add(runnable);
    }

    public static void shutdown() {
        for (Runnable runnable : SHUTDOWN_TASK) {
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("Shutdown hook error: ", e);
            }
        }
    }
}
