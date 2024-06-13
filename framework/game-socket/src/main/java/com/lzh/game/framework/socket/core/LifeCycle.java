package com.lzh.game.framework.socket.core;

public interface LifeCycle {

    void start();

    boolean isStared();

    void shutDown();

    void asyncStart();
}
