package com.lzh.game.framework.socket.core.bootstrap.handler;

import com.lzh.game.framework.socket.core.bootstrap.client.AbstractClient;
import com.lzh.game.framework.socket.core.protocol.HeartbeatProtocol;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.utils.Constant;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zehong.l
 * @since 2024-08-01 16:37
 **/
public class DefaultHeartbeatHandler implements HeartbeatHandler {

    private final ScheduledExecutorService service;

    private final AbstractClient<?> client;

    public DefaultHeartbeatHandler(AbstractClient<?> client) {
        this.client = client;
        this.service = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void start() {
        var mange = client.getContext().getSessionManage();
        mange.addConnectListener(this::connect);
        mange.addCloseListening(this::close);
    }

    @Override
    public void shutdown() {
        this.service.shutdownNow();
    }

    private void close(Session session) {
        cancelTask(session);
    }

    private void cancelTask(Session session) {
        var task = (ScheduledFuture<?>) session.getAttribute(Constant.HEARTBEAT_TASK_KEY);
        if (Objects.nonNull(task) && !task.isCancelled()) {
            task.cancel(false);
        }
    }

    private void connect(Session session) {
        cancelTask(session);
        long period = client.getProperties().getHeartbeatInterval();
        session.setAttribute(Constant.HEARTBEAT_TASK_KEY
                , service.scheduleAtFixedRate(new HeartbeatTask(session, client), period, period, TimeUnit.MILLISECONDS));
    }

    private record HeartbeatTask(Session session, AbstractClient<?> client) implements Runnable {

        @Override
        public void run() {
            if (!session.isActive()) {
                return;
            }
            client.oneWay(session, new HeartbeatProtocol());
        }
    }

}
