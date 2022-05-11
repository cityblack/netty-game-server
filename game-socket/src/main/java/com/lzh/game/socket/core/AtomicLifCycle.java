package com.lzh.game.socket.core;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicLifCycle implements LifeCycle {

    private static final byte NO_STARED = 0x00;
    private static final byte RUNNING = 0x01;
    private static final byte STARTED = 0x02;

    private final AtomicInteger STATUS = new AtomicInteger(NO_STARED);

    public boolean running() {
        return STATUS.compareAndSet(NO_STARED, RUNNING);
    }

    @Override
    public void start() {
        STATUS.compareAndSet(RUNNING, STARTED);
    }

    @Override
    public boolean isStared() {
        return STATUS.get() == STARTED;
    }

    @Override
    public void shutDown() {
        STATUS.compareAndSet(STARTED, NO_STARED);
    }

    @Override
    public void asyncStart() {
        STATUS.compareAndSet(RUNNING, STARTED);
    }
}
