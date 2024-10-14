package com.lzh.game.framework.socket.core.bootstrap;

public interface LifeCycle {

    void start();

    boolean isStared();

    boolean shutDown();

    void asyncStart();
}
