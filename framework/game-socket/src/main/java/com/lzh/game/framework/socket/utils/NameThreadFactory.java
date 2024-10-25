package com.lzh.game.framework.socket.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NameThreadFactory implements ThreadFactory {

    private final AtomicInteger theadNum = new AtomicInteger(1);

    private final String prefix;

    private final boolean daemon;

    private final ThreadGroup group;

    public NameThreadFactory(String prefix) {
        this(prefix, false);
    }

    public NameThreadFactory(String prefix, boolean daemon) {
        this.prefix = prefix;
        this.daemon = daemon;
        group = Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = prefix + "-" + theadNum.getAndIncrement();
        Thread thread = new Thread(group, r, name);
        thread.setDaemon(daemon);
        return thread;
    }
}
