package com.lzh.game.framework.socket.core.session.monitor;

import com.lzh.game.framework.socket.utils.NameThreadFactory;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionEvent;
import com.lzh.game.framework.socket.core.session.SessionManage;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class DefaultConnectMonitor implements ConnectMonitor {

    private final Map<String, MonitorMate> monitorContain = new ConcurrentHashMap<>();

    public DefaultConnectMonitor(SessionManage<Session> sessionManage) {
        sessionManage.addListener(SessionEvent.CONNECT, this::doConnect);
        sessionManage.addListener(SessionEvent.CLOSE, this::doCloseConnect);
    }

    private final Timer TASK_TIME = new HashedWheelTimer(new NameThreadFactory("session-monitor-re-connect"), 30, TimeUnit.MILLISECONDS);

    private void doConnect(Session session, Object o) {
        if (monitorContain.isEmpty()) {
            return;
        }
        var ip = session.getRemoteAddressStr();
        var mate = monitorContain.get(ip);
        if (Objects.isNull(mate)) {
            return;
        }
        mate.closeTimeout();
        mate.connected = true;
        log.info("{} connected. close monitor task", session.getId());
    }

    private void doCloseConnect(Session session, Object o) {
        if (monitorContain.isEmpty()) {
            return;
        }
        var ip = session.getRemoteAddressStr();
        var mate = monitorContain.get(ip);
        mate.connected = false;
        checkTask(ip);
    }

    @Override
    public void register(String ip, int port, ConnectMonitorConfig config, Consumer<String> notConnCall) {
        register(ip + ":" + port, config, notConnCall);
    }

    @Override
    public void register(String address, ConnectMonitorConfig config, Consumer<String> notConnCall) {
        this.monitorContain.put(address, new MonitorMate(address, config, notConnCall));
        checkTask(address);
    }

    @Override
    public void unRegister(String ip, int port) {
        unRegister(ip + ":" + port);
    }

    protected void checkTask(String address) {
        MonitorMate mate = this.monitorContain.get(address);
        if (Objects.isNull(mate)) {
            return;
        }
        mate.closeTimeout();
        if (mate.connected) {
            return;
        }
        if (mate.isOverRetry()) {
            log.info("[{}] retry to connect over limit. times:{}", address, mate.failCount);
            return;
        }
        long delayTime = mate.config.getCheckTimeInterval() * (mate.failCount + 1);
        mate.timeout = TASK_TIME.newTimeout(timeout -> {
            if (timeout.isCancelled()) {
                return;
            }
            doCheck(address, mate);
        }, delayTime, TimeUnit.MILLISECONDS);
    }

    private void doCheck(String address, MonitorMate mate) {
        try {
            log.info("Check {} connect. times:{}", address, mate.failCount);
            if (!mate.connected) {
                mate.addFailCount();
                log.info("{} didn't connected. retry to connect. retry times:{}", address, mate.failCount);
                mate.notConnectedCall.accept(address);
            } else {
                mate.clearFailCount();
            }
        } finally {
            checkTask(address);
        }
    }

    @Override
    public void unRegister(String address) {
        MonitorMate mate = this.monitorContain.remove(address);
        if (Objects.isNull(mate)) {
            return;
        }
        if (Objects.nonNull(mate.timeout)) {
            mate.timeout.cancel();
        }
    }

    private final static class MonitorMate {
        String address;
        ConnectMonitorConfig config;
        Consumer<String> notConnectedCall;
        Timeout timeout;
        int failCount;
        volatile boolean connected;

        public MonitorMate(String address, ConnectMonitorConfig config, Consumer<String> notConnectedCall) {
            this.address = address;
            this.config = config;
            this.notConnectedCall = notConnectedCall;
        }

        public void addFailCount() {
            this.failCount++;
        }

        public void clearFailCount() {
            this.failCount = 0;
        }

        public boolean isOverRetry() {
            return this.config.getReConnMaxCount() > 0
                    && this.failCount > this.config.getReConnMaxCount();
        }

        public void closeTimeout() {
            if (Objects.nonNull(timeout)) {
                if (!timeout.isCancelled() && !timeout.isExpired()) {
                    timeout.cancel();
                }
                timeout = null;
            }
        }
    }
}
