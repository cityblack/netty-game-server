package com.lzh.game.socket.core.coder;

public interface RemotingCmd {

    int cmd();

    Object data();

    byte[] byteData();
}
