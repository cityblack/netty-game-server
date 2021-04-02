package com.lzh.game.socket.core;

public interface Response {

    int OK = 0;

    int FAIL = 1;

    Object data();

    int cmd();

    int status();
}
