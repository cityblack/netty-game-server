package com.lzh.game.socket;

public interface GameServer {

    void asyncStart();

    void start();

    void stop();

    int getPort();

}
