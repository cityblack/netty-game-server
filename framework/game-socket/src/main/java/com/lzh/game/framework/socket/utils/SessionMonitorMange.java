package com.lzh.game.framework.socket.utils;

import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.NameThreadFactory;
import io.netty.channel.Channel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class SessionMonitorMange<S extends Session>
        implements SessionMonitor, SessionManage<S> {

    private final static Boolean CONNECTED = Boolean.TRUE;
    private final static Boolean UN_CONNECT = Boolean.FALSE;

    private SessionManage<S> manage;

    private Map<String, MonitorObjectMate> monitorContain = new ConcurrentHashMap<>();

    private Map<String, Boolean> connect = new ConcurrentHashMap<>();

    private static final Timer TASK_TIME = new HashedWheelTimer(new NameThreadFactory("session-monitor-re-connect", true), 30, TimeUnit.MILLISECONDS);

    public SessionMonitorMange(SessionManage<S> manage) {
        this.manage = manage;
    }

    @Override
    public S createSession(Channel channel) {
        return manage.createSession(channel);
    }

    @Override
    public S getSession(String sessionId) {
        return manage.getSession(sessionId);
    }

    @Override
    public void updateLastAccessTime(S session) {
        manage.updateLastAccessTime(session);
    }

    @Override
    public void pushSession(String sessionId, S session) {
        manage.pushSession(sessionId, session);
        String address = session.getRemoteAddress();
        this.connect.put(address, CONNECTED);
        MonitorObjectMate mate = this.monitorContain.get(address);
        if (Objects.nonNull(mate) && Objects.nonNull(mate.timeout)) {
            mate.timeout.cancel();
        }
    }

    @Override
    public void addSessionCloseListening(Consumer<S> consumer) {
        manage.addSessionCloseListening(consumer);
    }

    @Override
    public boolean contain(String sessionId) {
        return manage.contain(sessionId);
    }

    @Override
    public boolean removeSession(String sessionId) {
        S session = getSession(sessionId);
        boolean b = manage.removeSession(sessionId);
        if (Objects.nonNull(session)) {
            String address = session.getRemoteAddress();
            this.connect.remove(address);
            if (monitorContain.containsKey(address)) {
                checkTask(address);
            }
        }
        return b;
    }

    @Override
    public void addConnectListener(Consumer<S> connected) {
        manage.addConnectListener(connected);
    }

    @Override
    public List<S> getAllSession() {
        return manage.getAllSession();
    }

    @Override
    public void register(String ip, int port, SeasonMonitorConfig config, Consumer<String> notConnCall) {
        register(ip + ":" + port, config, notConnCall);
    }

    @Override
    public void register(String address, SeasonMonitorConfig config, Consumer<String> notConnCall) {
        this.monitorContain.put(address, new MonitorObjectMate(address, config, notConnCall));
        checkTask(address);
    }

    protected void checkTask(String address) {
        if (this.connect.getOrDefault(address, UN_CONNECT)) {
            return;
        }
        MonitorObjectMate mate = this.monitorContain.get(address);
        if (Objects.isNull(mate)) {
            return;
        }
        if (Objects.nonNull(mate.timeout)) {
            mate.timeout.cancel();
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

    private void doCheck(String address, MonitorObjectMate mate) {
        if (this.connect.getOrDefault(address, UN_CONNECT)) {
            return;
        }
        try {
            log.info("Check {} connect.", address);
            if (!this.connect.getOrDefault(address, UN_CONNECT)) {
                mate.addFailCount();
                log.info("{} is not connected. retry to connect. retry times:{}", address, mate.failCount);
                mate.notConnectedCall.accept(address);
            } else {
                mate.clearFailCount();
            }
        } finally {
            checkTask(address);
        }
    }

    @Override
    public void unRegister(String address, SeasonMonitorConfig config) {
        MonitorObjectMate mate = this.monitorContain.remove(address);
        if (Objects.isNull(mate)) {
            return;
        }
        if (Objects.nonNull(mate.timeout)) {
            mate.timeout.cancel();
        }
    }

    protected static class MonitorObjectMate {
        String address;
        SeasonMonitorConfig config;
        Consumer<String> notConnectedCall;

        Timeout timeout;
        int failCount;

        public MonitorObjectMate(String address, SeasonMonitorConfig config, Consumer<String> notConnectedCall) {
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
    }
}
