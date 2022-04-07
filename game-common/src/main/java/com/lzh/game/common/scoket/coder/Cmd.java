package com.lzh.game.common.scoket.coder;

public interface Cmd {

    int cmd();

    Object data();

    byte[] byteData();
}
