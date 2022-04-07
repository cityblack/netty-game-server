package com.lzh.game.common.scoket;

import com.lzh.game.common.scoket.coder.Cmd;

public interface Response extends Cmd {

    int OK = 0;

    int FAIL = 1;

    Object data();

    int status();
}
