package com.lzh.game.framework.socket.core.bootstrap;

public interface LifeCycle {

    void start();

    boolean isStared();

    void shutDown();

    void asyncStart();
}
