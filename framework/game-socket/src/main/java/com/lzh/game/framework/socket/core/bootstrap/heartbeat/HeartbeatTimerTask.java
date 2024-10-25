package com.lzh.game.framework.socket.core.bootstrap.heartbeat;

import com.lzh.game.framework.socket.utils.NameThreadFactory;
import com.lzh.game.framework.socket.core.protocol.HeartbeatProtocol;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.Constant;
import com.lzh.game.framework.socket.utils.ShutdownHook;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author zehong.l
 * @since 2024-10-21 12:24
 **/
@Slf4j
public class HeartbeatTimerTask implements Runnable {

    private static final HeartbeatProtocol PROTOCOL = new HeartbeatProtocol();

    private static final ScheduledExecutorService HEARTBEAT_TASK_TIME = Executors
            .newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), new NameThreadFactory("heartbeat-task"));

    static {
        ShutdownHook.register(HeartbeatTimerTask::shutdown);
    }

    private final Session session;

    public HeartbeatTimerTask(Session session) {
        this.session = session;
    }

    public static void newTimerTask(Session session, int interval) {
        // Prevent generating a large number of tasks at the same time
        int delay = ThreadLocalRandom.current().nextInt(0, 500);
        newTimerTask0(session, interval + delay, interval);
    }

    private static void newTimerTask0(Session session, int delay, int interval) {
        ScheduledFuture<?> task = session.getAttribute(Constant.HEARTBEAT_TASK_KEY);
        if (Objects.nonNull(task)) {
            task.cancel(false);
        }
        task = HEARTBEAT_TASK_TIME.scheduleAtFixedRate(new HeartbeatTimerTask(session), delay, interval, TimeUnit.MILLISECONDS);
        session.setAttribute(Constant.HEARTBEAT_TASK_KEY, task);
    }

    public static void removeTimerTask(Session session) {
        ScheduledFuture<?> task = session.getAttribute(Constant.HEARTBEAT_TASK_KEY);
        if (Objects.nonNull(task)) {
            task.cancel(false);
        }
    }

    @Override
    public void run() {
        try {
            session.oneWay(PROTOCOL);
        } catch (Exception e) {
            log.error("Send heartbeat error:", e);
        }
    }

    private static void shutdown() {
        HEARTBEAT_TASK_TIME.shutdownNow();
    }
}
