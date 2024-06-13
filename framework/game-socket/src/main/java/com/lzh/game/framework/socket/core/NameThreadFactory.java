package com.lzh.game.framework.socket.core;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NameThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

    private final AtomicInteger theadNum = new AtomicInteger(1);

    private final String prefix;

    private final boolean daemon;

    private final ThreadGroup group;

    public NameThreadFactory() {
        this("pool-" + POOL_SEQ.getAndIncrement(), false);
    }

    public NameThreadFactory(String prefix) {
        this(prefix, false);
    }

    public NameThreadFactory(String prefix, boolean daemon) {
        this.prefix = prefix;
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        group = Objects.isNull(s) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = prefix + theadNum.getAndIncrement();
        Thread thread = new Thread(group, r, name);
        thread.setDaemon(daemon);
        return thread;
    }
}
