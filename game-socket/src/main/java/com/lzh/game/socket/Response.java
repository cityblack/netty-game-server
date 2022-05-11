package com.lzh.game.socket;

public interface Response extends RemotingCommand {

    int OK = 0;

    int FAIL = 1;

    int TIMEOUT = 2;

    int status();
}
