package com.lzh.game.socket;

public interface RemotingCmd {

    int cmd();

    byte[] byteData();

    Object data();

    int commandKey();
}
